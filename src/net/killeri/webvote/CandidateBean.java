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

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * Representation of a candidate, who must belong to a party.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="Candidate" type="CMP" jndi-name="ejb/webvote/Candidate"
 * 
 * @ejb:finder signature="Collection findAll()" query="SELECT OBJECT(o) FROM
 *             Candidate o" unchecked="true"
 * 
 * @ejb:finder signature="Collection findByElection(java.lang.String
 *             electionName)" query="SELECT OBJECT(c) FROM Election e,
 *             IN(e.candidates) c WHERE e.name=?1" unchecked="true"
 * @jboss:query signature="Collection findByElection(java.lang.String
 *              electionName)" query="SELECT OBJECT(c) FROM Election e,
 *              IN(e.candidates) c WHERE e.name=?1 ORDER BY c.number" 
 *              unchecked="true"
 * 
 * @ejb:finder signature="Collection findByPartyNumber(java.lang.String
 *             partyName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *             c.party.name = ?1 AND c.party.election.name=c.election.name ORDER BY c.number" unchecked="true"
 * @jboss:query signature="Collection findByPartyNumber(java.lang.String
 *              partyName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *              c.party.name = ?1 AND c.party.election.name=c.election.name ORDER BY c.number" unchecked="true"
 * 
 * @ejb:finder signature="Collection findByVotes(java.lang.String partyName)"
 *             query="SELECT OBJECT(c) FROM Candidate c WHERE c.party.name = ?1
 *             AND c.party.election.name=c.election.name ORDER BY c.votes DESC" unchecked="true"
 * @jboss:query signature="Collection findByVotes(java.lang.String partyName)"
 *              query="SELECT OBJECT(c) FROM Candidate c WHERE c.party.name = ?1
 *              AND c.party.election.name=c.election.name ORDER BY c.votes DESC" unchecked="true"
 * 
 * @ejb:finder signature="Collection findByPartyOrderByVotes(java.lang.String
 *             partyName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *             c.party.name=?1 AND c.party.election.name=c.election.name ORDER BY c.votes DESC" unchecked="true"
 * @jboss:query signature="Collection findByPartyOrderByVotes(java.lang.String
 *              partyName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *              c.party.name like ?1 AND c.party.election.name=c.election.name ORDER BY c.votes DESC" unchecked="true"
 * 
 * @ejb:finder signature="Collection findByPartyComparison(java.lang.String election, java.lang.String
 *             partyName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *             c.election.name = ?1 and c.party.name = ?2" unchecked="true"
 * @jboss:query signature="Collection findByPartyComparison(java.lang.String
 *              partyName, java.lang.String)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *              c.election.name = ?1 and c.party.name = ?2 ORDER BY c.partyComparison DESC"
 *              unchecked="true"
 * 
 * @ejb:finder signature="Collection
 *             findByCoalitionAndPartyComparison(java.lang.String
 *             coalitionName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *             c.party.coalition.name = ?1 AND c.party.election.name=c.election.name" unchecked="true"
 * @jboss:query signature="Collection
 *              findByCoalitionAndPartyComparison(java.lang.String
 *              coalitionName)" query="SELECT OBJECT(c) FROM Candidate c WHERE
 *              c.party.coalition.name = ?1 AND c.party.election.name=c.election.name ORDER BY c.partyComparison DESC"
 *              unchecked="true"
 * 
 * @ejb:finder signature="Collection
 *             findByCoalitionComparisonVote(java.lang.String coalitionName)"
 *             query="SELECT OBJECT(c) FROM Candidate c WHERE
 *             c.party.coalition.name = ?1 AND c.party.election.name=c.election.name" unchecked="true"
 * @jboss:query signature="Collection
 *              findByCoalitionComparisonVote(java.lang.String coalitionName)"
 *              query="SELECT OBJECT(c) FROM Candidate c WHERE
 *              c.party.coalition.name = ?1 AND c.party.election.name=c.election.name ORDER BY c.votes DESC"
 *              unchecked="true"
 * 
 * @ejb:finder signature="Collection findByCoalitionComparison(java.lang.String
 *             election)" query="SELECT OBJECT(c) FROM Election e,
 *             IN(e.candidates) c WHERE e.name = ?1" unchecked="true"
 * @jboss:query signature="Collection findByCoalitionComparison(java.lang.String
 *              election)" query="SELECT OBJECT(c) FROM Election e,
 *              IN(e.candidates) c WHERE e.name = ?1 ORDER BY c.coalitionComparison DESC"
 *              unchecked="true"
 * 
 * @ejb:finder signature="Collection findByAllComparison(java.lang.String election)" 
 *             query="SELECT OBJECT(c) FROM Election e, IN(e.candidates) c
 *             WHERE e.name = ?1" unchecked="true"
 * @jboss:query signature="Collection findByAllComparison(java.lang.String election)" 
 *              query="SELECT OBJECT(c) FROM Election e, IN(e.candidates) c
 *              WHERE e.name = ?1 ORDER BY c.allComparison DESC"
 *              unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.Candidate"
 * 
 * @ejb:persistence table-name="Candidate"
 * 
 * @ejb:value-object match="*" name="Candidate"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class CandidateBean implements EntityBean {

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public CandidatePK ejbCreate(ElectionLocal election, int number, String lastName,
            String firstName) throws CreateException {
        CandidatePK pk = new CandidatePK( election.getName(), number );
        setFirstName( firstName );
        setLastName( lastName );
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
     * @ejb.relation name="Candidate-Election" role-name="Candidate-belongs-Election"
     * 
     * @jboss.relation fk-column="electionname" related-pk-field="name"
     */
    public abstract ElectionLocal getElection();

    public abstract void setElection(ElectionLocal party);

    /**
     * @ejb.pk-field
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="number"
     * @ejb.value-object
     */
    public abstract int getNumber();

    public abstract void setNumber(int number);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="lastName"
     * @ejb.value-object
     */
    public abstract String getLastName();

    /**
     * @ejb.interface-method
     */
    public abstract void setLastName(String lastName);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="firstName"
     * @ejb.value-object
     */
    public abstract String getFirstName();

    /**
     * @ejb.interface-method
     */
    public abstract void setFirstName(String firstName);

    /**
     * @ejb.interface-method
     */
    public String getName() {
        if( getLastName() == null ) return getFirstName();
        if( getFirstName() == null ) return getLastName();
        return getLastName() + " " + getFirstName();
    }

    /**
     * @ejb.interface-method
     * @ejb.relation name="Candidate-Party" role-name="Candidate-belongs-Party"
     * 
     * @jboss.relation fk-column="partyname" related-pk-field="name"
     * @jboss.relation fk-column="electionname" related-pk-field="electionName"
     * 
     * @jboss.relation-mapping style="foreign-key"
     */
    public abstract PartyLocal getParty();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setParty(PartyLocal party);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="random"
     * @ejb.value-object
     */
    public abstract double getRandom();

    /**
     * @ejb.interface-method
     */
    public abstract void setRandom(double random);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="votes"
     * @ejb.value-object
     */
    public abstract long getVotes();

    /**
     * @ejb.interface-method
     */
    public abstract void setVotes(long votes);

    /**
     * @ejb.interface-method
     */
    public void addVote() {
        setVotes( getVotes() + 1 );
    }

    /**
     * @ejb.interface-method
     */
    public void addVotes(long votes) {
        setVotes( getVotes() + votes );
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="partyComparison"
     * @ejb.value-object
     */
    public abstract Float getPartyComparison();

    /**
     * @ejb.interface-method
     */
    public abstract void setPartyComparison(Float comparison);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="coalitionComparison"
     * @ejb.value-object
     */
    public abstract Float getCoalitionComparison();

    /**
     * @ejb.interface-method
     */
    public abstract void setCoalitionComparison(Float comparison);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="allComparison"
     * @ejb.value-object
     */
    public abstract Float getAllComparison();

    /**
     * @ejb.interface-method
     */
    public abstract void setAllComparison(Float comparison);

    /**
     * @ejb.interface-method
     */
    public abstract CandidateValue getCandidateValue();

}
