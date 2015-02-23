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
 * Representation of a STV vote.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="STVVote" type="CMP" jndi-name="ejb/webvote/STVVote"
 * 
 * @ejb:finder signature="Collection findAll()" 
 *             query="SELECT OBJECT(o) FROM STVVote o" 
 *             unchecked="true" 
 * @jboss:query 
 *             signature="Collection findAll()" 
 *             query="SELECT OBJECT(o) FROM STVVote o ORDER BY o.id, o.candidate" 
 *             unchecked="true"
 * 
 * @ejb:finder signature="Collection findAllByRank()" 
 *             query="SELECT OBJECT(o) FROM STVVote o" 
 *             unchecked="true" 
 * @jboss:query
 *             signature="Collection findAllByRank()"
 *             query="SELECT OBJECT(o) FROM STVVote o ORDER BY o.id, o.rank" 
 *             unchecked="true"
 * 
 * @ejb:finder signature="Collection findByElection(java.lang.String e)" 
 *             query="SELECT OBJECT(o) FROM STVVote o WHERE o.election.name = ?1" 
 *             unchecked="true" 
 * @jboss:query
 *             signature="Collection findByElection(java.lang.String e)"
 *             query="SELECT OBJECT(o) FROM STVVote o WHERE o.election.name = ?1 ORDER BY o.id, o.rank" 
 *             unchecked="true"
 * 
 * ejb:finder signature="Collection findByElection(java.lang.String
 *             electionName)" query="SELECT OBJECT(o) FROM STVVote" unchecked="true"
 * jboss:query signature="Collection findByElection(java.lang.String
 *              electionName)" query="SELECT OBJECT(o) FROM STVVote o ORDER BY o.id, o.rank" 
 *              unchecked="true"
 * 
 * ejb:finder signature="Collection findByCandidate(int vote)" query="SELECT
 * OBJECT(o) FROM STVVote o WHERE o.id = ?1" unchecked="true" jboss:query
 * signature="Collection findByCandidate(int vote)" query="SELECT OBJECT(o) FROM
 * STVVote o WHERE o.id = ?1 ORDER BY o.candidate" unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.STVVote"
 * 
 * @ejb:persistence table-name="STVVote"
 * 
 * @ejb:value-object match="*" name="STVVote"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class STVVoteBean implements EntityBean {

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public Object ejbCreate(ElectionLocal election, String id)
            throws CreateException {
        setElectionName(election.getName());
        setRealId(id);
        return null;
    }

    /**
     * @ejb.pk-field
     * @ejb.interface-method view-type="local"
     * @ejb.persistence column-name="electionname"
     */
    public abstract String getElectionName();
    
    public abstract void setElectionName(String name);
    
    /**
     * @ejb.interface-method view-type="local"
     * @ejb.relation name="Election-STV" role-name="STV-belongs-Election"
     * @jboss.relation fk-column="electionname" related-pk-field="name"
     * @jboss.relation-mapping style="foreign-key"
     */
    public abstract ElectionLocal getElection();

    public abstract void setElection(ElectionLocal election);

    /**
     * @ejb.pk-field
     * @ejb.interface-method
     * @ejb.persistence column-name="realid"
     * @ejb.value-object
     */
    public abstract String getRealId();

    public abstract void setRealId(String id);
    
    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="id"
     * @ejb.value-object
     */
    public abstract String getId();

    /**
     * @ejb.interface-method
     */
    public abstract void setId(String id);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="candidate"
     * @ejb.value-object
     */
    public abstract int getCandidate();

    /**
     * @ejb.interface-method
     */
    public abstract void setCandidate(int cand);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="rank"
     * @ejb.value-object
     */
    public abstract int getRank();

    /**
     * @ejb.interface-method
     */
    public abstract void setRank(int r);

    /**
     * @ejb.interface-method
     */
    public abstract STVVoteValue getValue();
}
