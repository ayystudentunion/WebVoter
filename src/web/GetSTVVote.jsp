<%
    int places = 15;
    if( ! election.isProduction() || ! election.isFirstPassword() || LoginCheck.check(person, null, null, null) )
    {
		Collection candidates = candidateHome.findByElection(electionS);
		int candidateCount = candidates.size();
		places = candidateCount;
        String previousParty = "";
        StringBuffer candidateString = new StringBuffer( "\"-\", " );
        StringBuffer options = new StringBuffer();
        StringBuffer candidateList = new StringBuffer();
		Iterator i = candidates.iterator();
        int j = 1;
		while(i.hasNext()) {
			Candidate c = (Candidate) i.next();
			String name = c.getName();
			String party = vote.findPartyByCandidate( c.getNumber() ).getName();
			int number = c.getNumber();
            if( party != null && ! previousParty.equals( party ) )
            {
                previousParty = party;
                candidateString.append( "\"" + party + "\", " );
                options.append( "<option value=\"" + j + "\">" + party + "</option>\n" );
                candidateList.append( "<br/>" + party + "<br/>" );
                j++;
            }
            candidateString.append( "\"  " + number + " " + name + "\", " );
            options.append( "<option value=\"" + j + "\">&nbsp;&nbsp;" + number + "&nbsp;" + name + "</option>\n" );
            candidateList.append( number + " " + name + "<br/>" );
            j++;
        }
        options.append( "<option value=\"" + j + "\">(kaikki&nbsp;muut)</option>\n" );
                
%>
   <script language="JavaScript">
    /*
     * This script is copyright (c) 2004 by Jarno Elonen <elonen@iki.fi>.
     * All rights reserved (for the time being -- Open Source licensing
     * is planned later).
     */

        var n_options = <%=j+1%>;
        var others_idx = n_options-1;

        var n_places = <%=places%>;
        var last_place = n_places-1;

        var bubble_enabled = true;
        var successive_swap_cycles = 0;

        var old_val_on_focus = -1;

        var votes = new Array(n_places);
        for ( i=0; i<n_places; i++ )
            votes[i] = 0;

        var shared_flags = new Array(n_places);
        for ( i=0; i<n_places; i++ )
            shared_flags[i] = 0;

        var cands = new Array(<%=candidateString.toString()%>"(kaikki muut)", false);


        // Try to find an object with given name
        function Find_Obj( name )
        {
            doc = document;
            if ( doc[name] )
                return doc[name];
            if ( doc.all && doc.all[name] )
                return doc.all[name];
            if ( doc.getElementById && doc.getElementById(name))
                return doc.getElementById(name);
            for (var i=0; i<doc.forms.length; i++ )
                if ( doc.forms[i][name] )
                    return doc.forms[i][name];
            for (var i=0; i<doc.anchors.length; i++ )
                if ( doc.anchors[i].name == name )
                    return doc.anchors[u];
            return null;
        }

        // Returns location [x,y] of given object (reference)
        function Get_Loc( obj )
        {
            var x=0, y=0;
            if (obj.offsetParent)
            {
                while (obj.offsetParent)
                {
                    x += obj.offsetLeft
                    y += obj.offsetTop
                    obj = obj.offsetParent;
                }
            }
            else if (obj.x || obj.y)
            {
                x += obj.x;
                y += obj.y;
            }
            return [x,y];
        }

        // Returns size [x,y] of given object (reference)
        function Get_Size( obj )
        {
            var w=24, h=24;
            if (obj.offsetWidth)
            {
                w = obj.offsetWidth;
                h = obj.offsetHeight;
            }
            return [w,h];
        }

        // Returns viewport Y-bounds [top,bottom]
        function Get_Viewport_Y_Bounds()
        {
            var top=-1, bottom=-1;

            if (window.innerHeight)
            {
                top = window.pageYOffset;
                bottom = top + window.innerHeight;
            }
            else if (document.documentElement && document.documentElement.scrollTop)
            {
                top = document.documentElement.scrollTop;
                bottom = document.documentElement.scrollBottom;
            }
            else if (document.body)
            {
                top = document.body.scrollTop;
                bottom = document.body.scrollBottom;
            }
            return [top, bottom];
        }

        // Returns [left, top, right, bottom] for object with given name or null if none was found
        function Get_Bounds( name )
        {
            var obj = Find_Obj( name );
            if( !obj )
                return null;
            if( obj.style )
                obj = obj.style;

            var x = parseInt( obj.left ) ? parseInt( obj.left ) : 0;
            var y = parseInt( obj.top ) ? parseInt( obj.top ) : 0;

            if ( x == 0 && y == 0 )
            {
                o = obj;
                if ( o.offsetParent )
                {
                    for( var x = 0, y = 0; o.offsetParent; o = o.offsetParent )
                    {
                        x += o.offsetLeft;
                        y += oLink.offsetTop;
                    }
                }
                else if ( o.x || o.y )
                {
                    x = o.x;
                    y = o.y;
                }
            }

            if ( obj.clip && typeof( obj.clip.bottom ) == 'number' )
                return [theDiv.clip.left, theDiv.clip.top, theDiv.clip.right, theDiv.clip.bottom];
            if ( typeof( obj.pixelWidth ) != 'undefined' )
                return [x, y, x+obj.pixelWidth, y+obj.pixelHeight];
            if ( typeof( obj.width ) != 'undefined' && typeof( obj.height ) != 'undefined' )
                return [x, y, x+parseInt(obj.width), y+obj.parseInt(height)];
            return [x,y,x+16,y+16];
        }

        // Move layer to given location and set it visible/invisible
        function Move_Layer( id, x, y)
        {
            var obj = Find_Obj( id );
            if( !obj )
            {
                ShowMessage("Virhe - Ei oliota: " + id);
                return;
            }
            if( obj.style )
                obj = obj.style;
            var pix = document.childNodes ? 'px' : 0;
            obj.left = x + pix;
            obj.top = y + pix;
        }

        function Set_Layer_Vis( id, visible )
        {
            var obj = Find_Obj( id );
            if( !obj )
            {
                ShowMessage("Virhe - Ei oliota: " + id);
                return;
            }
            if( obj.style )
                obj = obj.style;
            obj.visibility = visible ? 'visible' : 'hidden';
        }

        function TrimStr( txt )
        {
            while(txt.length > 0 && txt.charAt(txt.length-1) == ' ' || txt.charCodeAt(txt.length-1) == 0xA0)
                txt = txt.substring(0, txt.length-1);
            while(txt.length > 0 && txt.charAt(0) == ' ' || txt.charCodeAt(0) == 0xA0)
                txt = txt.substring(1);
            return txt;
        }

        function VoteText(i)
        {
            return TrimStr(cands[VoteVal(i)]);
        }

        function VoteVal(i)
        {
            return votes[i];
        }

        var coded_change_guard = 0;
        function SetVote(i, sel)
        {
            coded_change_guard++;
            votes[i] = sel;
            document.ballotform.elements["place_" + i].value = cands[sel];
            coded_change_guard--;
        }

        function PlaceDivided(i)
        {
            return shared_flags[i];
        }

        function SetPlaceDivided(i, divided)
        {
            shared_flags[i] = divided;
            new_val = "" + (i+1) + ".";
            if ( divided )
                new_val = "...";
            document.ballotform.elements["shared_" + i].value = new_val;
        }

        function SwapPlaces(a,b)
        {
            var tmp = VoteVal(a);
            SetVote(a, VoteVal(b));
            SetVote(b, tmp);
        }

        function LastNonEmptyIdx()
        {
            for ( i=last_place; i>=0; i-- )
                if ( VoteVal(i) > 0 )
                    return i;
            return -1;
        }

        function ShowMessage( txt )
        {
            window.status = txt;
        }

        function ShowMessageOf( idx )
        {
            if ( VoteVal(idx) == 0 ) // dont' show anything for '-'
                return;
            if ( idx == 0 )
                ShowMessage( "'" + VoteText(idx) + "' on ensimm�inen valintasi."  );
            else if ( LastNonEmptyIdx() == idx )
                ShowMessage( "'" + VoteText(idx) + "' on VIIMEINEN valintasi." );
            else if ( VoteVal(idx-1) != 0 && VoteVal(idx+1) != 0)
                ShowMessage( "Asetat ehdokkaan '" + VoteText(idx) + "' ennen ehdokasta '" + VoteText(idx+1) +
                             "' mutta ehdokkaan '" + VoteText(idx-1) + "' ennen ehdokasta '" + VoteText(idx) + "'." );
            else if ( VoteVal(idx+1) != 0 )
                ShowMessage( "Asetat ehdokkaan '" + VoteText(idx) + "' ennen ehdokasta '" + VoteText(idx+1) + "'." );
            else if ( VoteVal(idx-1) != 0 )
                ShowMessage( "Asetat ehdokkaan '" + VoteText(idx-1) + "' ennen ehdokasta '" + VoteText(idx) + "'." );
        }

        function Remove_Dupes( idx )
        {
            var sel = VoteVal(idx);
            if ( sel > 0 )
                for ( i=0; i<n_places; i++ )
                    if ( i != idx && VoteVal(i) == sel )
                    {
                        SetVote(i, 0);
                        oldplace = i;
                        newplace = idx;
                        if ( i < idx )
                            newplace -= 1; // compensate for the empty place
                        else
                            oldplace -= 1; // compensate for the added element
                        if ( newplace != oldplace )
                            ShowMessage( "Ehdokas '" + VoteText(idx) + "' siirretty sijalta " + (oldplace+1) +
                                         " sijalle " + (newplace+1) + "." );
                    }
        }

        function Add_Others()
        {
            var has_others = false;
            for ( i=0; i<n_places; i++ )
                if ( VoteVal(i) == others_idx )
                {
                    has_others = true;
                    break;
                }
            if ( !has_others && LastNonEmptyIdx() < last_place )
                SetVote( LastNonEmptyIdx()+1, others_idx );
        }

        function ScrollDownFrom( idx )
        {
            coded_change_guard++;
            var old_bubble_e = bubble_enabled;
            start = LastNonEmptyIdx() + 1;
            if (start>last_place)
                start = last_place;
            for ( i=start; i>idx; i-- )
            {
                SetVote(i, VoteVal(i-1));
                SetPlaceDivided(i, PlaceDivided(i-1));
            }
            bubble_enabled = old_bubble_e;
            coded_change_guard--;
        }

        function Bubble()
        {
            var did_switch = false;
            var iterations = successive_swap_cycles;
            if ( iterations < 1 )
                iterations = 1;

            while( bubble_enabled && iterations-- > 0 )
            {
                var last_was_empty = false;
                did_switch = false;
                for ( var i=0; i<n_places && bubble_enabled; i++ )
                {
                    var sel = VoteVal(i);
                    var cur_is_empty = ( sel == 0 );
                    if ( last_was_empty && !cur_is_empty )
                    {
                        SetPlaceDivided(i-1, PlaceDivided(i));
                        SetPlaceDivided(i, 0);
                        SwapPlaces( i, i-1 );
                        did_switch = true;
                    }
                    last_was_empty = cur_is_empty;
                }
                if ( did_switch )
                    successive_swap_cycles++;
                else
                {
                    successive_swap_cycles = 0;
                    break;
                }
            }

            if ( !did_switch )
                bubble_enabled = false;
        }

        function Bubble_Timer()
        {
            Bubble();
            if ( bubble_enabled )
                window.setTimeout("Bubble_Timer()", 200);
        }

        function Start_Bubble()
        {
            bubble_enabled = true;
            Bubble_Timer();
        }

        function Remove_Handler( i )
        {
            ShowMessage( "Poistettu ehdokas '" + VoteText(i) + "'." );
            SetVote(i, 0);
            Start_Bubble();
        }
        function Up_Handler( i )
        {
            SwapPlaces(i, i-1);
            ShowMessageOf(i-1);
            Start_Bubble();
        }
        function Down_Handler( i )
        {
            SwapPlaces(i, i+1);
            ShowMessageOf(i+1);
            Start_Bubble();
        }

        function Clear_All()
        {
            for ( i=0; i<n_places; i++ )
            {
                SetVote(i, 0);
                SetPlaceDivided(i, 0);
            }
            ShowMessage( "��nestyslippu tyhjennetty." );
        }

        function Toggle_Shared( idx )
        {
            ShowMessage("Toggle " + idx);
            if ( !PlaceDivided(idx))
            {
                SetPlaceDivided(idx, 1);
                ShowMessage( "Sija " + (idx+1) + " on nyt jaettu sijan " + idx + " kanssa." );
            }
            else
            {
                SetPlaceDivided(idx, 0);
                ShowMessage( "Sija " + (idx+1) + " on nyt erillinen sijasta " + idx + "." );
            }
        }

        var cand_selector_at = -1;
        function Place_Click_Handler( i )
        {
            if ( cand_selector_at == i )
            {
                cand_selector_at = -1;
                Hide_Cand_Selector();
            }
            else
            {
                cand_selector_at = i;
                var butt = Find_Obj( "place_" + i );
                var loc = Get_Loc( butt );

                var butt_sz = Get_Size(butt);

                // Try to keep the popup inside the browser frame
                var sz = Get_Size(Find_Obj("candsel"));
                var y_bounds = Get_Viewport_Y_Bounds();
                if ( y_bounds[1] > 0 && loc[1]+sz[1] > y_bounds[1] )
                    loc[1] -= (sz[1] + butt_sz[1]);

                Move_Layer( "candspopup", loc[0], loc[1] + butt_sz[1] );
                Set_Layer_Vis( "candspopup", true );
                Find_Obj("candsel").focus()

                // Restore scrolling offset in case the
                // browser tried to be smart and decided
                // to scroll the viewport
                var new_y_bounds = Get_Viewport_Y_Bounds();
                if ( new_y_bounds[0] != y_bounds[0] )
                    scrollTo(0, y_bounds[0]);
            }
        }

        function Hide_Cand_Selector()
        {
            Move_Layer('candspopup', -500,0);
            Set_Layer_Vis( 'candspopup', false );
            idx = cand_selector_at;
            Remove_Dupes(idx);
            Start_Bubble();
        }

        function Cand_Sel_Handler()
        {
            idx = cand_selector_at;
            window.setTimeout("Hide_Cand_Selector(); cand_selector_at = -1;", 100);

            sel = Find_Obj("candsel").value;

            bubble_enabled = false;
            if ( VoteVal(idx) > 0 && sel > 0 )
                    ScrollDownFrom(idx);
            SetVote(idx, sel);

            // Add 'all others' before the selection if
            // there is empty space before it
            //if ( idx >= 2 && VoteVal(idx-1) == 0 && VoteVal(idx-2) == 0 )
            //{
            //    var has_others = false;
            //    for ( var i=0; i<n_places; i++ )
            //        if ( VoteVal(i) == others_idx )
            //        {
            //            has_others = true;
            //            break;
            //        }
            //    if ( !has_others )
            //        SetVote(idx-1, others_idx);
            //}

            ShowMessageOf( idx );
            Remove_Dupes( idx );
            //Add_Others();
            Start_Bubble();
            ShowMessage( "Changed idx " + idx);
            
        }

        function Set_Votes_Field()
        {
            // Clean up the ballot form first
            bubble_enabled = true;
            while ( bubble_enabled )
                Bubble();

            var res = "";
            for ( var i=0; i<n_places; i++ )
                if ( VoteVal(i) > 0 )
                {
                    var p = i;
                    while ( PlaceDivided(p))
                        p -= 1;
                    res = res + "" + (p+1) + "-" + VoteVal(i) + "_";
                }
            document.ballotform.elements["vote"].value = res;
        }

        function Prefill_Form()
        {
        }

    </script>

<table width="100%">
<tr><td>Aseta haluamasi ehdokkaat tai vaaliliitot paremmuusj�rjestykseen, 
ensimm�iselle sijalle eniten kannattamasi. Voit laittaa useita ehdokkaita
samalle sijaluvulle, jolloin jokainen ehdokas saa yht� suuren osan ��nest�si.
</td></tr>
<tr><td valign="top">
  <form name="ballotform" action="ConfirmVote.jsp" accept-charset="ISO-8859-1"
method="POST" onSubmit="Set_Votes_Field();"/>
  <div class="vote" onMouseDown="Hide_Cand_Selector();">
  <input type="hidden" name="vote" value="" />
      <table valign="top" border="0" cellspacing="2" cellpadding="0">
      
        <tr><td><input class="vote" type="submit" name="action" value="Jatka" /></td></tr>
<%
		for( j=0; j<places; j++ )
		{
		    String disabled = "disabled";
		    if( j>0 ) disabled = "onClick=\"Toggle_Shared(" + j + ");\"";
%>
        <tr>
          <td width="25"><input type="button" name="shared_<%=j%>" value="<%=j+1%>." <%=disabled%> title="Vaihda paikan jako"/></td>
          <td><input type="button" style="width: 200px; position: relative;" name="place_<%=j%>" onClick="Place_Click_Handler(<%=j%>);" value="-" title="Klikkaa lis�t�ksesi ehdokas paikalle <%=j+1%>"></td>
          <td><a href="#" target="_self" onClick="Remove_Handler(<%=j%>); return false;" title="Poista"><img border="0" src="list-del.gif" alt="Poista"></a></td>
<%
		    if( j<places-1 )
		    {
%>
<td><a href="#" target="_self" onClick="Down_Handler(<%=j%>); return false;"><img border="0" src="list-down.gif" alt="Alas"  title="Siirr� alasp�in"></a></td>
<%
                    } else {
%>
<td>&nbsp;</td>
<%
		    }
		    if( j>0 )
		    {
%>
<td><a href="#" target="_self" onClick="Up_Handler(<%=j%>); return false;"><img border="0" src="list-up.gif" alt="Yl�s" title="Siirr� yl�sp�in"></a></td>
<%
                    } else {
%>
<td>&nbsp;</td>
<%
		    }
%>
</tr>
<%
		  
		}
%>
    </table>
    </div>
    <div id="candspopup" style="background: white; visibility: hidden; display: block; position: absolute; left: 16px; top: 16px; z-index: 200; border: black thin solid;">
      <select size="10" name="candsel" name="candsel" onChange="Cand_Sel_Handler();" onBlur="Hide_Cand_Selector();">
<option value="0">-</option>
<%=options%>
      </select>
    </div>
</form>
    </td>
    <td valign="top" class="tdAnswer"><b>Ehdokaslista</b><br/><%=candidateList%></td>
    </tr></table>
<%
    } else {

%>
<div class="login">
  <form action="GetVote.jsp" accept-charset="ISO-8859-1"
method="POST" />
    <table>
      <tr>
        <td colspan="2" bgcolor="#bfbfff">
			Tuntematon k�ytt�j�, v��r� salasana tai olet jo ��nest�nyt. Yrit� uudestaan.
        </td>
      </tr>
      <tr>
        <td>Tunnus:</td>
        <td><input type="text" name="uid" /></td>
      </tr>
      <tr>
        <td>Salasana:</td>
        <td><input type="password" name="passwd" /></td>
      </tr>
      <tr>
        <td colspan="2">
          <input type="submit" name="action" value="Kirjaudu" />
        </td>
      </tr>
    </table>
  </form>
</div>
<%
	}
%>
<%@ include file="VoteFooter.jsp" %>
