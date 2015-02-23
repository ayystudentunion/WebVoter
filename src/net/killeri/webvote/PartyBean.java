/*
 Copyright (C) 2003-2005 Kalle Kivimaa (kalle.kivimaa@iki.fi)

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; either version 2.1 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.killeri.webvote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
 
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Representation of a party, which belongs to an election and possibly to a
 * coalition. Contains one or more candidates.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="Party" type="CMP" jndi-name="ejb/webvote/Party"
 * 
 * @ejb:finder signature="Collection findByElection(java.lang.String election)"
 *             query="SELECT OBJECT(p) FROM Election e, IN(e.partys) p WHERE e.name=?1"
 *             unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.Party"
 * 
 * @ejb:persistence table-name="Party"
 * 
 * @ejb:value-object match="*" name="Party"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class PartyBean implements EntityBean {

    private static Logger log = Logger.getLogger( PartyBean.class );

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public PartyPK ejbCreate(ElectionLocal election, String name) throws CreateException {
        PartyPK pk = new PartyPK( election.getName(), name );
        return pk;
    }

    /**
     * @ejb.pk-field
     * @ejb.persistent-field
     * @ejb.interface-method view-type="local"
     * @ejb.persistence column-name="electionname"
     */
    public abstract String getElectionName();
    
    public abstract void setElectionName(String name);
    
    /**
     * @ejb.interface-method view-type="local"
     * @ejb.relation name="Election-Party" role-name="Party-belongs-Election"
     * @jboss.relation fk-column="electionname" related-pk-field="name"
     */
    public abstract ElectionLocal getElection();

    public abstract void setElection(ElectionLocal election);

    /**
     * @ejb.pk-field
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="name"
     * @ejb.value-object
     */
    public abstract String getName();

    public abstract void setName(String name);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="votes"
     * @ejb.value-object
     */
    public abstract long getVotes();

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="votes"
     */
    public abstract void setVotes(long votes);

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation name="Candidate-Party" role-name="Party-has-Candidate"
     */
    public abstract Collection getCandidates();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setCandidates(Collection candidates);

    /**
     * @ejb.interface-method
     */
    public Collection getCandidatesByVotes() {
        try {
            return CandidateUtil.getLocalHome().findByPartyOrderByVotes(
                    getName() );
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        } catch( NamingException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public Collection getCandidatesByPartyComparison( String election ) {
        try {
            return CandidateUtil.getLocalHome().findByPartyComparison(
                    election, getName() );
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        } catch( NamingException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public Collection getCandidatesByCoalitionComparison(String election) {
        try {
            return CandidateUtil.getLocalHome().findByCoalitionComparison(
                    election );
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        } catch( NamingException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
    }

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.relation name="Party-Coalition" role-name="Party-belongs-Coalition"
     * @jboss.relation fk-column="coalitionname" related-pk-field="name"
     * @jboss.relation fk-column="electionname" related-pk-field="electionName"
     * @jboss.relation-mapping style="foreign-key"
     */
    public abstract CoalitionLocal getCoalition();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setCoalition(CoalitionLocal coalition);

    /**
     * @ejb.interface-method
     */
    public String getCoalitionName() {
        return ( getCoalition() == null ? null : getCoalition()
                .getName() );
    }

    /**
     * @ejb.interface-method
     */
    public void calculateComparisons() {
        Collection candidates = getCandidatesByVotes();
        Collection realCandidates = new ArrayList();
        Iterator i = candidates.iterator();
        setVotes( 0 );
        while( i.hasNext() ) {
            CandidateLocal c = (CandidateLocal) i.next();
            if( ! c.getElection().getName().equals( getElection().getName() ) ) continue;
            System.out.println("Adding " + getName() + " by " + c.getVotes() + " from " + getVotes() );
            setVotes( getVotes() + c.getVotes() );
            realCandidates.add( c );
        }
        Class[] emptyClassArray = new Class[0];
        try {
            calculateComparisons( realCandidates, CandidateLocal.class.getMethod(
                    "getVotes", emptyClassArray ), CandidateLocal.class
                    .getMethod( "setPartyComparison",
                            new Class[] { Float.class } ), getVotes(), false );
        } catch( SecurityException e ) {
            log.error( "Got exception", e );
        } catch( NoSuchMethodException e ) {
            log.error( "Got exception", e );
        } catch( IllegalArgumentException e ) {
            log.error( "Got exception", e );
        } catch( IllegalAccessException e ) {
            log.error( "Got exception", e );
        } catch( InvocationTargetException e ) {
            log.error( "Got exception", e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public void calculateComparisons(Collection candidates, Method orderMethod,
            Method comparisonMethod, long sumVotes, boolean useFloat)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        Class[] emptyClassArray = new Class[0];
        Iterator i = candidates.iterator();
        int currentDivisor = 1;
        CandidateLocal previous = null;
        CandidateLocal next = null;
        if( i.hasNext() )
            next = (CandidateLocal) i.next();
        while( next != null ) {
            if( previous != null
                    && ( useFloat ? ( (Float) orderMethod.invoke( previous,
                            emptyClassArray ) ).floatValue() == ( (Float) orderMethod
                            .invoke( next, emptyClassArray ) ).floatValue()
                            : ( (Long) orderMethod.invoke( previous,
                                    emptyClassArray ) ).longValue() == ( (Long) orderMethod
                                    .invoke( next, emptyClassArray ) )
                                    .longValue() ) ) {
                Set list = new HashSet();
                list.add( previous );
                list.add( next );
                boolean breakOut = false;
                while( !breakOut && i.hasNext() ) {
                    CandidateLocal cand = (CandidateLocal) i.next();
                    if( ( useFloat ? ( (Float) orderMethod.invoke( cand,
                            emptyClassArray ) ).floatValue() == ( (Float) orderMethod
                            .invoke( next, emptyClassArray ) ).floatValue()
                            : ( (Long) orderMethod.invoke( cand,
                                    emptyClassArray ) ).longValue() == ( (Long) orderMethod
                                    .invoke( next, emptyClassArray ) )
                                    .longValue() ) ) {
                        list.add( cand );
                    } else {
                        next = cand;
                        breakOut = true;
                    }
                }
                if( !breakOut )
                    next = null;
                Iterator j = list.iterator();
                while( j.hasNext() ) {
		    CandidateLocal cl = (CandidateLocal) j.next();
		    if( cl.getRandom() == 0 ) {
                        cl.setRandom( CoalitionBean.RAND
                            .nextDouble() );
		    }
		}
                Object[] o = list.toArray();
                for( int k = 0; k < o.length; k++ ) {
                    for( int l = k + 1; l < o.length; l++ ) {
                        CandidateLocal c1 = (CandidateLocal) o[k];
                        CandidateLocal c2 = (CandidateLocal) o[l];
                        if( c1.getRandom() < c2.getRandom() ) {
                            o[k] = c2;
                            o[l] = c1;
                        }
                    }
                }
                currentDivisor--;
                for( int k = 0; k < o.length; k++ ) {
                    CandidateLocal c = (CandidateLocal) o[k];
                    comparisonMethod.invoke( c, new Object[] { new Float(
                            ( (float) sumVotes ) / currentDivisor ) } );
                    currentDivisor++;
                }
            } else {
                previous = next;
                comparisonMethod.invoke( next, new Object[] { new Float(
                        ( (float) sumVotes ) / currentDivisor ) } );
                currentDivisor++;
                if( i.hasNext() ) {
                    next = (CandidateLocal) i.next();
                } else {
                    next = null;
                }
            }
        }
    }

    /**
     * @ejb.interface-method
     */
    public abstract PartyValue getPartyValue();

}
