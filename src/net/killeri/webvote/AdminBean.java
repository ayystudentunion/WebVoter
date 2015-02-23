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
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.apache.log4j.Logger;

/**
 * Provides various admin functionality, which for one reason or another cannot
 * be performed directly thorugh the bean remote interfaces in the JSP layer.
 * 
 * @ejb:bean name="Admin" type="Stateless" jndi-name="Admin"
 * 
 * @author Kalle Kivimaa
 */
public abstract class AdminBean implements SessionBean {

    private static Logger log = Logger.getLogger( AdminBean.class );

    protected ElectionLocalHome electionHome;

    protected CoalitionLocalHome coalitionHome;

    protected PartyLocalHome partyHome;

    protected CandidateLocalHome candidateHome;

    protected PersonLocalHome personHome;

    protected STVVoteLocalHome stvVoteHome;
    
    protected ElectionLocal election;

    protected VoteLocal voteBean;
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() {

        try {
            javax.naming.InitialContext initialContext = new javax.naming.InitialContext();
            Object objRef = initialContext.lookup( "ElectionLocal" );
            electionHome = (ElectionLocalHome) PortableRemoteObject
                    .narrow( objRef, ElectionLocalHome.class );
            objRef = initialContext.lookup( "CoalitionLocal" );
            coalitionHome = (CoalitionLocalHome) PortableRemoteObject
                    .narrow( objRef, CoalitionLocalHome.class );
            objRef = initialContext.lookup( "PartyLocal" );
            partyHome = (PartyLocalHome) PortableRemoteObject.narrow(
                    objRef, PartyLocalHome.class );
            objRef = initialContext.lookup( "CandidateLocal" );
            candidateHome = (CandidateLocalHome) PortableRemoteObject
                    .narrow( objRef, CandidateLocalHome.class );
            objRef = initialContext.lookup( "PersonLocal" );
            personHome = (PersonLocalHome) PortableRemoteObject
                    .narrow( objRef, PersonLocalHome.class );
            objRef = initialContext.lookup( "STVVoteLocal" );
            stvVoteHome = (STVVoteLocalHome) PortableRemoteObject
                    .narrow( objRef, STVVoteLocalHome.class );
            objRef = initialContext.lookup( "VoteLocal" );
            voteBean = ((VoteLocalHome) PortableRemoteObject.narrow( objRef, VoteLocalHome.class )).create();
        } catch( NamingException e ) {
            log.error( "Bean not found", e );
            throw new EJBException( "Bean not found", e );
        } catch( ClassCastException e ) {
            log.error("Unable to create vote bean", e);
            throw new EJBException("Unable to create vote bean", e);
        } catch( CreateException e ) {
            log.error("Unable to create vote bean", e);
            throw new EJBException("Unable to create vote bean", e);
        }
    }

    /**
     * @ejb.interface-method
     */
    public void init( String electionName ) {
        try {
            election = electionHome.findByPrimaryKey( new ElectionPK( electionName ) );
            voteBean.init( electionName );
        } catch( FinderException e ) {
            log.error("Invalid election " + electionName, e);
            throw new EJBException( "Invalid election", e );
        }
    }
    
    /**
     * Calculate d'Hondt results.
     * 
     * @ejb.interface-method
     */
    public void calculateAll() {
        try {

            if( election.getIsOpen() )
                return;
            if( election.isSTV() )
                throw new EJBException( "This is an STV election!" );
            Collection candidates = candidateHome.findByElection( election.getName() );
            Iterator i = candidates.iterator();
            while( i.hasNext() ) {
                CandidateLocal cand = (CandidateLocal) i.next();
                cand.setPartyComparison( new Float( 0 ) );
                cand.setCoalitionComparison( new Float( 0 ) );
                cand.setAllComparison( new Float( 0 ) );
            }
            Collection coalitions = new HashSet();
            Collection parties = partyHome.findByElection( election.getName() );
            i = parties.iterator();
            while( i.hasNext() ) {
                CoalitionLocal c = ( (PartyLocal) i.next() ).getCoalition();
                if( c != null )
                    coalitions.add( c );
            }
            i = coalitions.iterator();
            while( i.hasNext() )
                ( (CoalitionLocal) i.next() ).calculateComparisons( election
                        .getGovernment() );
            i = parties.iterator();
            PartyLocal party = null;
            while( i.hasNext() ) {
                party = (PartyLocal) i.next();
                if( party.getCoalitionName() == null
                        || party.getCoalitionName().length() == 0 ) {
                    party.calculateComparisons();
                    candidates = candidateHome.findByPartyComparison( election.getName(), party
                            .getName() );
                    Iterator j = candidates.iterator();
                    while( j.hasNext() ) {
                        CandidateLocal cand = (CandidateLocal) j.next();
                        log.debug( "Setting coalition equal to party for "
                                + cand.getName() );
                        cand.setCoalitionComparison( cand.getPartyComparison() );
                    }
                }
            }

            // OK, everything has been calculated, now it remains to sort the
            // candidates.
            candidates = candidateHome.findByCoalitionComparison( election.getName() );
            Class[] emptyClassArray = new Class[0];
            party.calculateComparisons( candidates, CandidateLocal.class
                    .getMethod( "getCoalitionComparison", emptyClassArray ),
                    CandidateLocal.class.getMethod( "setAllComparison",
                            new Class[] { Float.class } ), 1000, true );
        } catch( FinderException e ) {
            log.error( "Cannot find data", e );
            throw new EJBException( e );
        } catch( IllegalAccessException e ) {
            log.error( "This should not happen!", e );
            throw new EJBException( e );
        } catch( InvocationTargetException e ) {
            log.error( "This should not happen!", e );
            throw new EJBException( e );
        } catch( NoSuchMethodException e ) {
            log.error( "This should not happen!", e );
            throw new EJBException( e );
        }
    }

    /**
     * STV calculation method
     * 
     * @ejb.interface-method
     */
    public synchronized List calculateResult(int elected) {
        SingleTransferrableVote stv = new SingleTransferrableVote();
        stv.debug = true;

        try {
            Collection l = stvVoteHome.findByElection(election.getName());
            log.debug("Found all votes.");
            Collection list = new TreeSet( new MyComparator() );
            list.addAll( l );
            log.debug("Votes in a comparator");
            Iterator it = list.iterator();
            String currentVote = null;
            int maxCandidate = 0;
            Map votes = new HashMap();
            List voteList = new ArrayList();
            while( it.hasNext() ) {
                STVVoteLocal voteS = (STVVoteLocal) it.next();
                //log.debug( "Comparing " + maxCandidate + " to " +
                //vote.getCandidate() );
                log
                        .debug( "Vote " + (voteS.getCandidate()) + "="
                                + voteS.getRank() );
                if( currentVote != null && !voteS.getId().equals( currentVote ) ) {
                    voteList.add( votes );
                    log.debug( "Added: " + votes );
                    log.debug( "New id: " + voteS.getId() );
                    votes = new HashMap();
                }
                votes.put( new Integer( voteS.getCandidate() ), new Integer(
                        voteS.getRank() ) );
                currentVote = voteS.getId();
                if( voteS.getCandidate() > maxCandidate ) maxCandidate = voteS.getCandidate();
            }
            log.debug( "Added last: " + votes );
            voteList.add( votes );
            for( int i = 0; i < voteList.size(); i++ ) {
                int[] voteArray = new int[maxCandidate];
                votes = (Map) voteList.get( i );
                for( int j = 1; j < maxCandidate + 1; j++ ) {
                    Integer rank = ( (Integer) votes.get( new Integer( j ) ) );
                    log.debug("Added " + rank + " to " + j);
                    if( rank != null )
                        voteArray[j - 1] = rank.intValue();
                }
                log.debug("Processed vote " + i);
                stv.addVotes( voteArray );
            }
        } catch( FinderException re ) {
            log.error( "Finder exception", re );
            throw new EJBException( "Unable to calculate", re );
        }
        log.debug("Votes processed, starting to calculate");
        List results = new ArrayList();
        results.addAll( Arrays.asList( stv.calculateResult( elected ) ) );
        log.debug("Votes calculated, adding remaining and dropped");
        results.addAll( stv.m_remainingCandidates );
        List dropped = stv.m_droppedCandidates;
        List droppedReverse = new ArrayList();
        Iterator i = dropped.iterator();
        while( i.hasNext() ) {
            droppedReverse.add( 0, i.next() );
        }
        results.addAll( droppedReverse );
        List realResults = new ArrayList();
        for( int j = 0; j < results.size(); j++ ) {
            Integer ii = (Integer) results.get( j );
            if( ii != null )
                realResults.add( new Integer( ii.intValue() + 1 ) );
        }
        return realResults;
    }
 
    /**
     * @ejb.interface-method
     */
    public Collection findCoalitionsByElection() {
        Collection coalitions = new HashSet();
        try {
            Collection parties = partyHome.findByElection( election.getName() );
            Iterator i = parties.iterator();
            while( i.hasNext() ) {
                PartyLocal p = (PartyLocal) i.next();
                if( p.getCoalition() != null ) {
                    coalitions.add( p.getCoalition().getName() );
                }
            }
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
        return coalitions;
    }

    /**
     * @ejb.interface-method
     */
    public Map showActivityByDate() {
        Map activity = new HashMap();
        Collection persons;
        try {
            persons = personHome.findAllVotedByElection( election.getName() );
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
        SimpleDateFormat f = new SimpleDateFormat( "d.M.yyyy" );
        Iterator i = persons.iterator();
        while( i.hasNext() ) {
            PersonLocal person = (PersonLocal) i.next();
            int voteType = person.getVoteStyle();
            Date d = person.getVoteDate();
            if( d == null ) continue;
            String dateString = f.format( d ).intern();
            List list = (List) activity.get( dateString );
            if( list == null ) {
                list = new ArrayList( voteType );
                for( int j = 0; j < voteType; j++ )
                    list.add( new Integer( 0 ) );
                list.add( new Integer( 1 ) );
                activity.put( dateString, list );
            } else {
                if( list.size() < voteType + 1 ) {
                    for( int j = list.size(); j < voteType - 1; j++ )
                        list.add( new Integer( 0 ) );
                    list.add( new Integer( 1 ) );
                } else {
                    Integer votes = (Integer) list.get( voteType );
                    list.set( voteType, new Integer( votes.intValue() + 1 ) );
                }
            }
        }
        return activity;
    }

    /**
     * @throws FinderException 
     * @ejb.interface-method
     */
    public void addBulkVotes(String votes) {
        if( election.isSTV() ) {
            addBulkSTVVotes( votes );
            return;
        }
        StringTokenizer st = new StringTokenizer( votes, " ,\t\n\r\f", false );
        while( st.hasMoreTokens() ) {
            String candidate = st.nextToken().trim();
            String voteS = st.nextToken().trim();
            try {
                CandidateLocal cand = candidateHome
                        .findByPrimaryKey( new CandidatePK( election.getName(),
                                Integer.parseInt( candidate ) ) );
                cand.addVotes( Long.parseLong( voteS ) );
            } catch( NumberFormatException e ) {
                log.error( "Invalid number format in " + candidate + " or "
                        + voteS + "(length " + voteS.length() + ")" );
                throw new EJBException( e );
            } catch( FinderException e ) {
                log.error( "Unable to find Candidate with " + candidate );
                throw new EJBException( e );
            }
        }
    }

    protected void addBulkSTVVotes(String votes) {
        Map voteMap = new HashMap();
        StringTokenizer st = new StringTokenizer( votes, "\n" );
        int lineCounter = 1;
        while( st.hasMoreTokens() ) {
            String line = st.nextToken().trim();
            if( line.startsWith( "." ) || line.startsWith( "-" ) ) {
                log.info( "Adding vote " + voteMap );
                voteBean.addVote( voteMap );
                voteMap = new HashMap();
                lineCounter = 1;
            } else {
                StringTokenizer sst = new StringTokenizer( line, ", " );
                while( sst.hasMoreTokens() ) {
                    String candidate = sst.nextToken();
                    voteMap.put( new Integer( Integer.parseInt( candidate ) ),
                            new Integer( lineCounter ) );
                }
                lineCounter++;
            }
        }
    }

    /**
     * @ejb.interface-method
     */
    public void addBulkPersons(String persons) {
        StringTokenizer st = new StringTokenizer( persons, " ,\n", false );
        while( st.hasMoreTokens() ) {
            String number = st.nextToken();
            String firstName = st.nextToken();
            String lastName = st.nextToken();
            String address = "";
            String zip = "";
            String city = "";
            String password = number;
            try {
                PersonLocal person = personHome.create( election, number,
                        lastName, firstName );
                person.setAddress( address );
                person.setCity( city );
                person.setZipCode( zip );
                person.setPassword( password );
            } catch( CreateException e ) {
                log.error( "Unable to create person " + number );
                throw new EJBException( e );
            }
        }
    }

    /**
     * Unmark a voter. This should only used in testing.
     * 
     * @ejb.interface-method
     */
    public void cancelVoter(String personNumber) {
        try {
            PersonLocal person = personHome.findByPrimaryKey( new PersonPK(
                    election.getName(), personNumber ) );
            person.setHasVoted( false );
        } catch( FinderException e ) {
            log.error( "Unable to find person " + personNumber );
            throw new EJBException( e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public void markAsVoted(String personNumber, int voteStyle) {
        try {
            PersonLocal person = personHome.findByPrimaryKey( new PersonPK(
                    election.getName(), personNumber ) );
            synchronized( person ) {
                if( ! person.getHasVoted() ) {
                    person.setHasVoted( true );
                    person.setVoteDate( new Date() );
                    person.setVoteStyle( voteStyle );
                }
            }
        } catch( FinderException e ) {
            log.error( "Unable to find person " + personNumber );
            throw new EJBException( e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public long getVotedCount() {
        try {
            if( election.isSTV() ) {
                Collection stvVotes = stvVoteHome.findByElection( election.getName() );
                long count = 0;
                String lastID = null;
                Iterator i = stvVotes.iterator();
                while( i.hasNext() ) {
                    STVVoteLocal s = (STVVoteLocal) i.next();
                    if( ! s.getId().equals( lastID ) ) {
                        count++;
                        lastID = s.getId();
                    }
                }
                return count;
            }
            Collection persons = personHome
                .findAllVotedByElection( election.getName() );
            return persons.size();
        } catch( FinderException e ) {
            throw new EJBException( "Cannot find bean", e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public long getPersonCountByElection() {
        try {
            Collection persons = personHome.findAllByElection( election.getName() );
            return persons.size();
        } catch( FinderException e ) {
            throw new EJBException( "Cannot find bean", e );
        }
    }

    public class MyComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            STVVoteLocal s1 = (STVVoteLocal) o1;
            STVVoteLocal s2 = (STVVoteLocal) o2;
            if( s1.getId().compareTo( s2.getId() ) == 0 ) {
                if( s1.getCandidate() < s2.getCandidate() )
                    return -1;
                else if( s1.getCandidate() > s2.getCandidate() )
                    return 1;
                else
                    return 0;
            }
            return s1.getId().compareTo( s2.getId() );
        }
    }
}
