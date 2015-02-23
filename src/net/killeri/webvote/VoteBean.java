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

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Business mehtods for voting.
 * 
 * @ejb:bean name="Vote" type="Stateful" jndi-name="ejb/webvote/Vote"
 * 
 * @author Kalle Kivimaa
 */
public abstract class VoteBean implements SessionBean {

    private static Logger log = Logger.getLogger( AdminBean.class );

    PersonLocalHome personHome = null;

    ElectionLocalHome electionHome = null;

    PartyLocalHome partyHome = null;

    CandidateLocalHome candidateHome = null;

    PersonLocal person = null;

    STVVoteLocalHome stvVoteHome = null;
    
    ElectionLocal election = null;

    boolean authenticated = false;

    /**
     * @ejb.create-method
     */
    public void ejbCreate() {
        javax.naming.InitialContext initialContext = null;
        try {
            initialContext = new javax.naming.InitialContext();
            Object objRef = initialContext
                    .lookup( "ElectionLocal" );
            electionHome = (ElectionLocalHome) javax.rmi.PortableRemoteObject
                    .narrow( objRef, ElectionLocalHome.class );
            objRef = initialContext.lookup( "PartyLocal" );
            partyHome = (PartyLocalHome) javax.rmi.PortableRemoteObject
                    .narrow( objRef, PartyLocalHome.class );
            objRef = initialContext.lookup( "CandidateLocal" );
            candidateHome = (CandidateLocalHome) javax.rmi.PortableRemoteObject
                    .narrow( objRef, CandidateLocalHome.class );
            objRef = initialContext.lookup( "PersonLocal" );
            personHome = (PersonLocalHome) javax.rmi.PortableRemoteObject
                    .narrow( objRef, PersonLocalHome.class );
            objRef = initialContext.lookup( "STVVoteLocal" );
            stvVoteHome = (STVVoteLocalHome) javax.rmi.PortableRemoteObject.narrow(
                    objRef, STVVoteLocalHome.class );
        } catch( NamingException e ) {
            log.error( "Bean not found", e );
            throw new EJBException( "Bean not found", e );
        }
    }

    /**
     * @ejb.interface-method
     */
    public void init( String electionName ) {
        try {
            election = electionHome.findByPrimaryKey( new ElectionPK( electionName ) );
        } catch( FinderException e ) {
            log.error("Invalid election " + electionName, e);
            throw new EJBException( "Invalid election", e );
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public boolean authenticate(String userNumber,
            String userPassword) {
        PersonPK pk = new PersonPK( election.getName(), userNumber );
        try {
            person = personHome.findByPrimaryKey( pk );
        } catch( FinderException e ) {
            log.error( "Cannot find person bean for " + userNumber, e );
            throw new EJBException( e );
        }
        if( election.isProduction() && election.isSecondPassword()
                && ( ! person.getPassword().equals( userPassword )
                        || person.getHasVoted() ) ) {
            authenticated = false;
        } else {
            authenticated = true;
        }
        return authenticated;
    }

    /**
     * @ejb.interface-method
     */
    public boolean vote(int candidate, int style) {
        if( ! election.getIsOpen() )
            return false;
        if( person == null )
            return false;
        synchronized( person ) {
            if( election.isProduction()
                    && ( !authenticated || person.getHasVoted() ) )
                return false;
            person.setHasVoted( true );
            person.setVoteDate( new Date() );
            person.setVoteStyle( style );
            try {
                if( !election.isProduction() )
                    log.info( "Voter " + person.getName() + " voting started for " + candidate + "." );
                candidateHome.findByPrimaryKey( new CandidatePK( election.getName(), candidate ) ).addVote();
                log.info( "Election [" + election.getName() + "] Voter ["
                        + person.getName() + "] has voted successfully." );
            } catch( FinderException e ) {
                log.error( "Cannot find candidate " + candidate, e );
                throw new EJBException( e );
            }
        }
        return true;
    }

    /**
     * STV election metod
     * 
     * @ejb.interface-method
     */
    public String vote(Map votes, int style) {
        if( ! election.getIsOpen() )
            return "Not open";
        String returnId;
        synchronized( person ) {
            if( !authenticated )
                return "Not authenticated";
            if( election.isProduction() && person.getHasVoted() )
                return "Already voted";
            person.setHasVoted( true );
            person.setVoteDate( new Date() );
            person.setVoteStyle( style );
            returnId = addVote( votes );
            log.info( "Election: " + election.getName() + " Voter "
                    + person.getName() + " has voted successfully." );
        }
        return returnId;
    }

    /**
     * STV election metod
     * 
     * @ejb.interface-method
     */
    public String addVote(Map votes) throws EJBException {
        if( election.getIsOpen() ) return "Election open";
        log.info( "Adding vote: " + votes );
        Iterator i = votes.keySet().iterator();
        String returnId = STVVoteUtil.generateGUID( this );
        while( i.hasNext() ) {
            String realId = STVVoteUtil.generateGUID( this );
            Integer candidate = (Integer) i.next();
            Integer rank = (Integer) votes.get( candidate );
            if( ! election.getProduction() ) {
                log.info( "Adding vote " + election.getName() + " " + candidate + ", " + rank + " " + returnId + " " + realId );
            }
            try {
                STVVoteLocal vote = stvVoteHome.create(election, realId);
                vote.setCandidate( candidate.intValue() );
                vote.setRank( rank.intValue() );
                vote.setId( returnId );
            } catch( CreateException e ) {
                log.error( "Got exception", e );
                throw new EJBException( "Unable to create vote", e );
            }
        }
        return returnId;
    }

    /**
     * @ejb.interface-method
     */
    public PartyValue findPartyByCandidate(int number) {
        PartyLocal party;
        try {
            party = candidateHome.findByPrimaryKey( new CandidatePK( election.getName(), number ) )
                    .getParty();
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
        return party.getPartyValue();
    }

    /**
     * @ejb.interface-method
     */
    public CoalitionValue findCoalitionByParty(String name) {
        CoalitionLocal coalition;
        try {
            coalition = partyHome.findByPrimaryKey( new PartyPK( election.getName(), name ) )
                    .getCoalition();
            if( coalition == null )
                return null;
        } catch( FinderException e ) {
            log.error( "Got exception", e );
            throw new EJBException( e );
        }
        return coalition.getCoalitionValue();
    }

}
