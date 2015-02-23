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

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * Representation of a person who belongs to an election.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="Person" type="CMP" jndi-name="ejb/webvote/Person"
 * 
 * @ejb:finder signature="Collection findAll()" query="SELECT OBJECT(o) FROM
 *             Person o" unchecked="true"
 * 
 * @ejb:finder signature="Collection findAllByElection(java.lang.String
 *             election)" query="SELECT OBJECT(p) FROM Election e,
 *             IN(e.persons) p WHERE e.name = ?1" unchecked="true"
 * 
 * @ejb:finder signature="Collection findAllVotedByElection(java.lang.String
 *             election)" query="SELECT OBJECT(p) FROM Election e,
 *             IN(e.persons) p WHERE e.name = ?1 AND p.hasVoted = true" 
 *             unchecked="true"
 * 
 * @ejb:finder signature="Collection findByNumber()" query="SELECT OBJECT(o)
 *             FROM Person o" unchecked="true"
 * @jboss:query signature="Collection findByNumber()" query="SELECT OBJECT(o)
 *              FROM Person o ORDER BY o.personNumber" unchecked="true"
 * 
 * @ejb:finder signature="Person findByUidPassword(java.lang.String uid,
 *             java.lang.String passwd)" query="SELECT OBJECT(o) FROM Person o
 *             WHERE o.personNumber=?1 AND o.password=?2" unchecked="true"
 * 
 * @ejb:finder signature="Collection findByPartialMatch(java.lang.String name,
 *             java.lang.String election)" query="SELECT OBJECT(p) 
 *             FROM Election e, IN(e.persons) p WHERE p.lastName like ?1 
 *             AND e.name = ?2" unchecked="true"
 * @jboss:query signature="Collection findByPartialMatch(java.lang.String name,
 *              java.lang.String election)" query="SELECT OBJECT(p) 
 *              FROM Election e, IN(e.persons) p WHERE p.lastName like ?1 
 *              AND e.name = ?2 ORDER BY p.lastName, p.firstName" 
 *              unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.Person"
 * 
 * @ejb:persistence table-name="Person"
 * 
 * @ejb:value-object match="*" name="Person"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class PersonBean implements EntityBean {

    public static final int VOTE_TRADITIONAL = 1;

    public static final int VOTE_WWW = 2;

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public PersonPK ejbCreate(ElectionLocal election, String number, String lastName,
            String firstName) throws CreateException {
        PersonPK pk = new PersonPK( election.getName(), number );
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
     * @ejb.relation name="Election-Person" role-name="Person-belongs-Election"
     * @jboss.relation fk-column="electionname" related-pk-field="name"
     */
    public abstract ElectionLocal getElection();

    public abstract void setElection(ElectionLocal election);

    /**
     * @ejb.pk-field
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="personNumber"
     */
    public abstract String getPersonNumber();

    /**
     * @ejb.interface-method
     */
    public abstract void setPersonNumber(String number);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="lastName"
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
        return getLastName() + " " + getFirstName();
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="emailAddress"
     */
    public abstract String getEmailAddress();

    /**
     * @ejb.interface-method
     */
    public abstract void setEmailAddress(String emalAddress);

    /**
     * ejb.persistent-field ejb.interface-method ejb.persistence
     * column-name="birthdate"
     */
    public abstract String getBirthdate();

    /**
     * ejb.interface-method
     */
    public abstract void setBirthdate(String date);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="address"
     */
    public abstract String getAddress();

    /**
     * @ejb.interface-method
     */
    public abstract void setAddress(String address);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="city"
     */
    public abstract String getCity();

    /**
     * @ejb.interface-method
     */
    public abstract void setCity(String city);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="zipCode"
     */
    public abstract String getZipCode();

    /**
     * @ejb.interface-method
     */
    public abstract void setZipCode(String zipCode);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="voteDate"
     */
    public abstract Date getVoteDate();

    /**
     * @ejb.interface-method
     */
    public abstract void setVoteDate(Date voteDate);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="hasVoted"
     */
    public abstract boolean getHasVoted();

    /**
     * @ejb.interface-method
     */
    public abstract void setHasVoted(boolean hasVoted);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="voteStyle"
     */
    public abstract int getVoteStyle();

    /**
     * @ejb.interface-method
     */
    public abstract void setVoteStyle(int voteStyle);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="password"
     */
    public abstract String getPassword();

    /**
     * @ejb.interface-method
     */
    public abstract void setPassword(String password);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="adminpassword"
     */
    public abstract String getAdminPassword();

    /**
     * @ejb.interface-method
     */
    public abstract void setAdminPassword(String password);

}
