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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.FinderException;
import javax.naming.NamingException;

/**
 * Representation of a coalition, which consists of one or more parties.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="Coalition" type="CMP" jndi-name="ejb/webvote/Coalition"
 * 
 * @ejb:finder signature="Collection findByElection(java.lang.String
 *             electionName)" query="SELECT OBJECT(c) FROM Election e, 
 *             IN(e.coalitions) c WHERE e.name=?1" unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.Coalition"
 * 
 * @ejb:persistence table-name="Coalition"
 * 
 * @ejb:value-object match="*" name="Coalition"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class CoalitionBean implements EntityBean {

    public static java.util.Random RAND = new java.util.Random( 112233 );

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public CoalitionPK ejbCreate(ElectionLocal election, String name) throws CreateException {
        CoalitionPK pk = new CoalitionPK( election.getName(), name );
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
     * @ejb.relation name="Election-Coalition" role-name="Coalition-belongs-Election"
     * @jboss.relation related-pk-field="name" fk-column="electionname"
     */
    public abstract ElectionLocal getElection();

    public abstract void setElection(ElectionLocal election);

    /**
     * @ejb.pk-field
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="name"
     */
    public abstract String getName();

    public abstract void setName(String name);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="votes"
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
     * @ejb.relation name="Party-Coalition" role-name="Coalition-has-Party"
     */
    public abstract Collection getPartys();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setPartys(Collection parties);

    /**
     * @ejb.interface-method
     */
    public void calculateComparisons(boolean government) {
        Collection parties = getPartys();
        Collection candidates;
        Iterator i = parties.iterator();
        setVotes( 0 );
        PartyLocal party = null;
        while( i.hasNext() ) {
            party = (PartyLocal) i.next();
            if( ! party.getElection().getName().equals( getElection().getName() ) ) continue;
            party.calculateComparisons();
            setVotes( getVotes() + party.getVotes() );
            System.out.println( "Setting " + getName() + "/" + party.getName() + " to " + party.getVotes() );
        }
        Class[] emptyClassArray = new Class[0];
        try {
            if( government ) {
                candidates = CandidateUtil.getLocalHome()
                        .findByCoalitionComparisonVote( getName() );
            } else {
                candidates = CandidateUtil.getLocalHome()
                        .findByCoalitionAndPartyComparison( getName() );
            }
            Collection realCandidates = new ArrayList();
            i = candidates.iterator();
            while( i.hasNext() ) {
                CandidateLocal c = (CandidateLocal) i.next();
                if( c.getElection().getName().equals( getElection().getName() ) ) realCandidates.add( c );
            }
            party.calculateComparisons( realCandidates, CandidateLocal.class
                    .getMethod( "getPartyComparison", emptyClassArray ),
                    CandidateLocal.class.getMethod( "setCoalitionComparison",
                            new Class[] { Float.class } ), getVotes(), true );
        } catch( IllegalArgumentException e ) {
            e.printStackTrace();
        } catch( SecurityException e ) {
            e.printStackTrace();
        } catch( IllegalAccessException e ) {
            e.printStackTrace();
        } catch( InvocationTargetException e ) {
            e.printStackTrace();
        } catch( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch( FinderException e1 ) {
            e1.printStackTrace();
            throw new EJBException( e1 );
        } catch( NamingException e1 ) {
            e1.printStackTrace();
            throw new EJBException( e1 );
        }
    }

    /**
     * @ejb.interface-method
     */
    public void calculateComparisons(Collection candidates) {
        Object[] c = candidates.toArray();
        for( int j = 0; j < c.length; j++ ) {
            for( int k = j + 1; k < c.length; k++ ) {
                CandidateLocal c1 = (CandidateLocal) c[j];
                CandidateLocal c2 = (CandidateLocal) c[k];
                if( c1.getPartyComparison().floatValue() < c2
                        .getPartyComparison().floatValue() ) {
                    c[j] = c2;
                    c[k] = c1;
                } else if( c1.getPartyComparison() == c2.getPartyComparison()
                        && RAND.nextBoolean() ) {
                    c[j] = c2;
                    c[k] = c1;
                }
            }
        }

        for( int j = 0; j < c.length; j++ ) {
            ( (CandidateLocal) c[j] ).setCoalitionComparison( new Float(
                    ( (float) getVotes() ) / ( j + 1 ) ) );
        }
    }

    /**
     * @ejb.interface-method
     */
    public Collection getCandidatesByNumber() {
        Collection parties = getPartys();
        ArrayList list = new ArrayList();
        Iterator i = parties.iterator();
        while( i.hasNext() ) {
            PartyLocal p = (PartyLocal) i.next();
            if( ! p.getElection().getName().equals( getElection().getName() ) ) continue;
            try {
                list.addAll( CandidateUtil.getLocalHome().findByPartyNumber(
                        p.getName() ) );
            } catch( FinderException e ) {
                throw new EJBException( e );
            } catch( NamingException e ) {
                throw new EJBException( e );
            }
        }
        return list;
    }

    /**
     * @ejb.interface-method
     */
    public Collection getCandidatesByPartyComparison() {
        Collection parties = getPartys();
        ArrayList list = new ArrayList();
        Iterator i = parties.iterator();
        while( i.hasNext() ) {
            PartyLocal p = (PartyLocal) i.next();
            if( ! p.getElection().getName().equals( getElection().getName() ) ) continue;
            try {
                list.addAll( CandidateUtil.getLocalHome()
                        .findByPartyComparison( getElection().getName(),
                                p.getName() ) );
            } catch( FinderException e ) {
                throw new EJBException( e );
            } catch( NamingException e ) {
                throw new EJBException( e );
            }
        }
        return list;
    }

    /**
     * @ejb.interface-method
     */
    public Collection getCandidatesByCoalitionComparison() {
        Collection parties = getPartys();
        ArrayList list = new ArrayList();
        Iterator i = parties.iterator();
        while( i.hasNext() ) {
            PartyLocal p = (PartyLocal) i.next();
            if( ! p.getElection().getName().equals( getElection().getName() ) ) continue;
            try {
                list.addAll( CandidateUtil.getLocalHome()
                        .findByCoalitionComparison(
                                p.getName() ) );
            } catch( FinderException e ) {
                throw new EJBException( e );
            } catch( NamingException e ) {
                throw new EJBException( e );
            }
        }
        return list;
    }

    /**
     * @ejb.interface-method
     */
    public abstract CoalitionValue getCoalitionValue();
}
