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

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * Representation of an election, which has parties and people in it.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="Election" type="CMP" jndi-name="ejb/webvote/Election"
 * 
 * @ejb:finder signature="Collection findAll()" query="SELECT OBJECT(o) FROM
 *             Election o" unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.Election"
 * 
 * @ejb:persistence table-name="Election"
 * 
 * @ejb:value-object match="*" name="Election"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class ElectionBean implements EntityBean {

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public ElectionPK ejbCreate(String name) throws CreateException {
        ElectionPK pk = new ElectionPK( name );
        return pk;
    }

    /**
     * @ejb.pk-field
     * @ejb.pk
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="name"
     */
    public abstract String getName();

    /**
     * @ejb.interface-method
     */
    public abstract void setName(String name);

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
     * @ejb.persistence column-name="authurl"
     */
    public abstract String getAuthenticationURL();

    /**
     * @ejb.interface-method
     */
    public abstract void setAuthenticationURL(String authURL);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="isopen"
     */
    public abstract boolean getIsOpen();

    /**
     * @ejb.interface-method
     */
    public boolean isOpen() {
        return getIsOpen();
    }

    /**
     * @ejb.interface-method
     */
    public abstract void setIsOpen(boolean isOpen);

    /**
     * @ejb.interface-method
     */
    public boolean isProduction() {
        return getProduction();
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="production"
     */
    public abstract boolean getProduction();

    /**
     * @ejb.interface-method
     */
    public abstract void setProduction(boolean isProduction);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="ispublic"
     */
    public abstract boolean getResultsPublic();

    /**
     * @ejb.interface-method
     */
    public boolean isPublic() {
        return getResultsPublic();
    }

    /**
     * @ejb.interface-method
     */
    public abstract void setResultsPublic(boolean isPublic);

    /**
     * @ejb.interface-method
     */
    public boolean isFirstPassword() {
        return getFirstPassword();
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="firstpassword"
     */
    public abstract boolean getFirstPassword();

    /**
     * @ejb.interface-method
     */
    public abstract void setFirstPassword(boolean isFirst);

    /**
     * @ejb.interface-method
     */
    public boolean isSecondPassword() {
        return getSecondPassword();
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="secondpassword"
     */
    public abstract boolean getSecondPassword();

    /**
     * @ejb.interface-method
     */
    public abstract void setSecondPassword(boolean second);

    /**
     * @ejb.interface-method
     */
    public boolean isSTV() {
        return getSTV();
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="stv"
     */
    public abstract boolean getSTV();

    /**
     * @ejb.interface-method
     */
    public abstract void setSTV(boolean stv);

    /**
     * @ejb.interface-method
     */
    public boolean isGovernment() {
        return getGovernment();
    }

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="government"
     */
    public abstract boolean getGovernment();

    /**
     * @ejb.interface-method
     */
    public abstract void setGovernment(boolean isOpen);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="toelect"
     */
    public abstract int getToElect();

    /**
     * @ejb.interface-method
     */
    public abstract void setToElect(int toElect);

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation name="Election-Coalition" role-name="Election-has-Coalition"
     */
    public abstract Collection getCoalitions();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setCoalitions(Collection coalitions);

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation name="Election-Party" role-name="Election-has-Party"
     */
    public abstract Collection getPartys();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setPartys(Collection partys);

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation name="Candidate-Election" role-name="Election-has-Candidate"
     */
    public abstract Collection getCandidates();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setCandidates(Collection candidates);

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation name="Election-Person" role-name="Election-has-Person"
     */
    public abstract Collection getPersons();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setPersons(Collection persons);

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation name="Election-STV" role-name="Election-has-STV"
     */
    public abstract Collection getSTVVotes();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setSTVVotes(Collection votes);

}
