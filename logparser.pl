#!/c/bin/perl
#
# Results parser for WebVoter written by Johannes Heinonen <hiero@iki.fi>.
#

# Information about candidates: id, full name, name of the coalition.
# Fields separated with candidate field separator (see below), 
# candidate records with newlines.
$CandidateInfo = "ehdokkaat.txt"; 
$CandidateFieldSeparator = ";";

# Location of WebVoter log stream.
$WebVoterLog = "webvoter.log";

# Generated HTML pages: prefix and number of digits for filenames.
$HtmlPagePath = "html/";
$HtmlPagePrefix = "2006_page_";
$HtmlPageDigits = 2;

# Show navigation links on generated pages
$PrintNaviLinks = 1;

# Open data streams
&init_html;
&read_candidate_names;
&open_stream;

# Process input stream
while () {
    last if (!&one_round);
    &print_html;
}
&print_html("last");

open(LOG1, ">log.remaining_vote_values.txt") || die "Failed to write a log file\n";
open(LOG2, ">log.elected_per_vote.txt") || die "Failed to write a log file\n";
my $c_removed = 0;
my $c_value = 0;
print LOG1 "Votes after vote counting is over:\n";
foreach my $vote (1..$#Votes) {
    my $value_left = $Votes[$vote];
    my $msg;
    if (exists $Removed{$vote}) {
	my ($vote, $value, $round) = @{ $Removed{$vote} };
	$msg = "has been REMOVED with value of $value on round $round";
	$value_left = $value;
	$c_removed++;
    } elsif ($Votes[$vote]) {
	$msg = "still has VALUE of $Votes[$vote]";
	$c_value++;
    } else {
	$msg = "has ZERO VALUE but is hasn't been removed yet";
    }
    printf LOG1 "Vote: $vote %.5f -- %s\n", $value_left, $msg;
}
printf LOG1 "Total: %d removed, %d with value, %d with zero value\n",
    $c_removed, $c_value, $#Votes - $c_removed - $c_value;
print LOG2 "Elected candidates for each vote:\n";
foreach my $vote (1..$#Votes) {
    my @cand = @{ $SubtractedFor[$vote] };
    my %coal;
    foreach my $cand (@cand) { $coal{$CandidateCoalitions{$cand}}++; };
    printf LOG2 "Vote $vote, Coalitions=%d, Count=%d: %s\n", 
	   scalar (keys %coal), scalar @cand, join(' ', sort {$a <=> $b} @cand);
}
close(LOG1);
close(LOG2);

# Close data streams and exit
&close_stream;
exit 0;

#################################################################

sub one_round {
    
    my ($foo, $id, $q, $v, $p) = &consume_up_to("Round:");
    return 0 if (!&has_stream);
    $q = substr($q, 2);
    $v = substr($v, 2);
    $p = substr($p, 2);

    my $round = (scalar @Rounds) + 1;
    my $prev_seen = $round>1 ? $Rounds[-1][4] : 0;
    my $log = "";

    # skip votes
    my $data;
    if ($round == 1) {
	$data = &fetch_next;
	while ($data =~ /^(\d+)/) {
	    $Votes[$1] = 1.0;
	    $data = &fetch_next;
	}
    } else {
	do { $data = &fetch_next; } while ($data =~ /^\d+/);
    }

    # vote counts for candidates
    my %seen;
    my @elected;
    my @data = &split_data($data);
    my @this_round;
	
    do { 
	if (exists $Candidates{$data[1]}) {
	    if ($data[2] =~ /has/) {
		$seen{$data[1]}++;
		push @this_round, [ $data[1], $data[3] ];
	    } elsif ($data[3] =~ /elected/) {
		push @elected, $data[1];
	    }
	} else {
	}
	@data = &fetch_next_splitted;
    } while ($data[0] =~ /Candidate/);

    # rest
    my $removed_v = 0; 
    my $multi_q_check = 0;
    $data = join(' ', @data);
    do {
	# subtracting due an elected candidate
	my $read_next = "true";
	if ($data =~ /^Subtracting/) {
	    my $sub_cand = $data[2];
	    my $q_check = 0; 
	    my $hitcount = 0;
	    @data = &fetch_next_splitted;
	    do {
		$Votes[$data[1]] = substr($data[-1], 4);
		if ($data[-2] =~ /^Subtracting/) {
		    $SubtractedFor[$data[1]] = [ @{ $SubtractedFor[$data[1]] }, $sub_cand ];
		    $q_check += substr($data[-2], 12);
		    $hitcount++;
		}
		@data = &fetch_next_splitted;
	    } while ($data[0] =~ /^Vote/);
	    $log .= sprintf("Candidate %d: %.5f (%.2f%% of Q=%.5f) from %d votes\n",
		   $sub_cand, $q_check, 100 * $q_check / $q, $q, $hitcount);
	    $multi_q_check += $q_check;
	    $data = join(' ', @data);
	    $read_next = 0;
	} 
	elsif ($data =~ /^Removing/) {
	    @data = split_data($data); 
	    if (! exists $Removed{$data[2]}) {
		$removed_v += $Votes[$data[2]];
		$Removed{$data[2]} = [ $data[2], $Votes[$data[2]], $round ];
		$Votes[$data[2]] = 0; 
	    }
	}
	$data = &fetch_next if $read_next;
    } while (&has_stream && $data !~ /^---/);
    
    # subtracting 
    
    my $seen = scalar keys %seen;

    # update state only if some changes were detected

    if ($seen && $seen != $prev_seen) {

	$log = "-" x 70 . "\nRound $round, Q=$q, V=$v, P=$p\n" . $log;
	$log .= @elected ? "Elected: " . join(', ', @elected) . "\n" : "Elimination round\n";

	my $v_check = 0; 
	foreach $vote (@Votes) { $v_check += $vote };
	$log .= sprintf("Checking vote count:\n\tElected:\t%.5f (real: %.5f)\n\t" .
			"Removed:\t%.5f\n\tIn votes:\t%.5f\n",
			(scalar @elected) * $q, $multi_q_check, $removed_v, $v_check);
	my $v_check_sum = $v_check + (scalar @elected) * $q + $removed_v;
	$log .= sprintf("\tTotal=%.5f vs. V=%.5f (%.2f%%)\n", 
			$v_check_sum, $v, 100.0 * $v_check_sum / $v);
	my $v_check_sum2 = $v_check + $multi_q_check + $removed_v;
	$log .= sprintf("\t\"Elected-Fixed\" Total=%.5f vs. V=%.5f (%.2f%%)\n",
			$v_check_sum2, $v, 100.0 * $v_check_sum2 / $v);

	# add this round to Rounds matrix
	push @Rounds, [ $round, $q, $v, $p, $seen, 
			sprintf("%.5f", $multi_q_check),
			sprintf("%.5f", $removed_v) ];
	
	# copy data from this round to Results matrix 
	for (my $idx = 0; $idx <= $#this_round; $idx++) {
	    my ($id, $votes) = @{ $this_round[$idx] };
	    my ($cand, $old_votes, $status, $active_round) = @{ $Results[$id] };
	    if ($votes > $q) {
		push @Elected, $id;
	    }
	    $Results[$id] = [ $id, $votes, $status, $round ];
	}
    }
    
    print $log;

    $seen;
}

sub mark_inactive {

    my $mark_round = shift @_;
    return if $mark_round < 1;

    # mark inactive candidates as either elected or eliminated
    foreach my $id (@Candidates) {
	my ($cand, $votes, $status, $active_round) = @{ $Results[$id] };
	if ($active_round == $mark_round && !$status) {
	    my $q_for_prev_round = $Rounds[-2][1];
	    my $new_status = grep(/^$id$/, @Elected) ? "valittu ($q_for_prev_round)" : "karsittu";
	    $Results[$id] = [ $id, $votes, $new_status, $active_round ];
	}
    }
}


#################################################################


sub open_stream {
    open(INPUT, "<$WebVoterLog") || die "Failed to open file $WebVoterLog for reading\n";
    $StreamReader = 0;
    $StreamData = 0;
}

sub has_stream {
    !eof(INPUT);
}

sub consume_up_to {
    my $look_for = pop(@_);
    my $line;
    while ($line = &fetch_next) {
	last if $line =~ /^$look_for/;
    }
    split(/[ :,]+/, $line);
}

sub fetch_next {
    $StreamReader++;
    $StreamData = <INPUT>;
    chomp($StreamData);
    $StreamData;
}

sub fetch_next_splitted {
    split_data(&fetch_next);
}

sub split_data {
    split(/[ :,]+/, pop(@_));
}

sub close_stream {
    close(INPUT);
    print "Done\n";
}

sub report_bad_data {
    print "Bad data on line $StreamReader: $StreamData\n";
}

sub read_candidate_names {
    open(NAMES, "<$CandidateInfo") || die "Failed to open file $CandidateInfo for reading\n";
    while (my $line = <NAMES>) {
	chomp $line;
	my @line = split(/$CandidateFieldSeparator/, $line);
	my $id = shift @line;
	$CandidateNames{$id} = shift @line;
	$CandidateCoalitions{$id} = shift @line;
	$Candidates{$id}++;
    }
    close(NAMES);
    @Candidates = sort {$a <=> $b} keys %Candidates;
}

#################################################################

sub min {
    my ($a, $b) = @_;
    $a < $b ? $a : $b;
}

sub get_order {
    sort { 
	my $a_elected = grep(/^$a$/, @Elected);
	my $b_elected = grep(/^$b$/, @Elected);
	my ($foo, $a_votes, $foo2, $a_round) = @{ $Results[$a] };
	my ($bar, $b_votes, $bar2, $b_round) = @{ $Results[$b] };
	if ($a_elected && $b_elected) {
	    # lower round over higher round, higher vote count over lower count
	    if ($a_round == $b_round) {
		$b_votes <=> $a_votes;
	    } else {
		$a_round <=> $b_round;
	    }
	} else {
	    # elected over non-elected, higher vote count over lower count
	    if ($a_elected == $b_elected) {
		$b_votes <=> $a_votes;
	    } else {
		$b_elected <=> $a_elected;
	    }
	}
    } @Candidates;
}

sub init_html {
    $first_page = $curr_page = "index.html";
    $next_page = sprintf("${HtmlPagePrefix}%0${HtmlPageDigits}d.html", ++$next_count);
    mkdir($HtmlPagePath);
}

sub print_html {
    my ($_round, $_q, $_v, $_p, $foo, $_consumed, $_removed) = @{ $Rounds[-1] };
    my $last = 1 if @_[0] =~ /last/;
    return 0 if (!$last && $_round == $prev_printed_round);
    $prev_printed_round = $_round;

    &mark_inactive($Rounds[-1][0] - ($last ? 0 : 1));

    $prev_page = $curr_page;
    $curr_page = $next_page;
    $next_page = sprintf("${HtmlPagePrefix}%0${HtmlPageDigits}d.html", ++$next_count);

    open(OUTPUT, ">${HtmlPagePath}/$curr_page") || die "Failed to open file $curr_page for writing\n";

    my $next_link = "| <a class=\"stv\" href=\"$next_page\">Seuraava sivu</a>" if (!$last);
    my $title = !$last ? "Kierros $_round" : "Tulosluettelo";
    my $header_para = <<__tmp__
<p class="stv">Laskennassa huomioitava äänimäärä: $_v<br>
Läpimenoon vaaditaan (kierroksen peruskiintiö): $_q
</p>
__tmp__
;

    $header_para = <<__tmp__
<h2 class="stv">$title</h2>
$header_para
__tmp__
;

    $header_para = <<__tmp__
$header_para
<p class="stv"><a class="stv" href="$first_page">Alkuun</a> | <a class="stv" href="$prev_page">Edellinen sivu</a> | $title $next_link</p>
__tmp__
if $PrintNaviLinks;
#<p><a href="index.html">Takaisin alkuun</a><br><br></p>
#<p><a href="$first_page">Alkuun</a> | <a href="$prev_page">Edellinen sivu</a> | $title $next_link</p>

    
    print OUTPUT <<__tmp2__
<html>
<head>
<link href="default.css" rel="stylesheet" type="text/css">
</head>
<body>
$header_para
<p class="stv">
<table border="1" cellspacing="0" cellpadding="1">
<tr width="730">
<th class="stv" width="30">Sija</th>
<th class="stv" width="140">Ehdokas</th>
<th class="stv" width="120">Vaaliliitto</th>
<th class="stv" width="30" class="center">Tunnus</th>
<th class="stv" width="100">Ääniä</th>
<th class="stv" width="30" class="center">Kierros</th>
<th class="stv" width="280">Tilanne</th></tr>
__tmp2__
;

    my @print_order = &get_order;
    my $rank;    
    while ($#print_order >= 0) {
	$rank++;
	my $i = shift @print_order;
	my ($_cand, $_votes, $_status, $_active_round) = @{ $Results[$i] };
	my $width = sprintf("%d%", 100 * $_votes / $_q) if (!$last && $_active_round == $_round);
	my $gauge;
	my $st = $_status ? $_status : $_votes;
	my $css;
#	my $css_class;
	$css = "pudotettu" if $st =~ /karsittu/;
	if (defined $width) {
	    $gauge = sprintf ("<img src=\"palkki.png\" width=\"%d\" height=\"10\" alt=\"\">", min($width,100));
	    $gauge .= sprintf ("<img src=\"palkki_yli.png\" width=\"%d\" height=\"10\" alt=\"\">", $width-100) if $width > 100;
	} elsif (grep(/^$i$/, @Elected)) {
	    $gauge = "<img src=\"palkki_valittu.png\" width=\"100\" height=\"10\" alt\"\">&nbsp;100%";
	    $css = "valittu";
	}
#	#if ($css) { $css_class = " class=\"stv_${css}\""; }

	print OUTPUT <<__tmp3__
<tr class="stv"><td class="stv_${css}center">$rank</td>
    <td class="stv_${css}"><nobr>&nbsp;$CandidateNames{$i}</nobr></td>
    <td class="stv_${css}"><nobr>&nbsp;$CandidateCoalitions{$i}</nobr></td>
    <td class="stv_${css}center">$i</td>
    <td class="stv_${css}oikea"><nobr>$st&nbsp;</nobr></td>
    <td class="stv_${css}center">$_active_round</td>
    <td class="stv_${css}"><nobr>&nbsp;$gauge&nbsp;$width</nobr></td></tr>
__tmp3__
;
    }

    print OUTPUT <<__tmp4__
</table>
</body>
</html>
__tmp4__
;

    close(OUTPUT);

    if ($last) {

	my $links;
	for (my $idx = 1; $idx < $next_count; $idx++) {
	    $links .= sprintf("<a class=\"stv\" href=\"${HtmlPagePrefix}%0${HtmlPageDigits}d.html\">" .
			      "%s</a><br>\n", $idx, 
			      $idx != $next_count-1 ? "Kierros $idx" : "Tulosluettelo");
	}

	open(OUTPUT, ">${HtmlPagePath}/index.html") || die "Failed to open file index.html for writing\n";
	print OUTPUT <<__tmp__
<html>
<head>
<link href="default.css" rel="stylesheet" type="text/css">
</head>
<body>

<h2 class="stv">Tuloslaskenta</h2>
<blockquote>
<p class="stv">$links</p>
</blockquote>	    
</body>
</html>
__tmp__
;	
	close OUTPUT;
    }
}

#################################################################
