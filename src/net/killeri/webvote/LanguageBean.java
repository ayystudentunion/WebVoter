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
 * Representation of a language specific data for the front end.
 * 
 * @author Kalle Kivimaa
 * 
 * @ejb:bean name="Language" type="CMP" jndi-name="ejb/webvote/Language"
 * 
 * @ejb:finder signature="Collection findAll()" query="SELECT OBJECT(o) FROM
 *             Language o" unchecked="true"
 * 
 * @ejb:interface remote-class="net.killeri.webvote.Language"
 * 
 * @ejb:persistence table-name="Language"
 * 
 * @ejb:value-object match="*" name="Language"
 * 
 * @ejb:util generate="physical"
 * 
 * @ejb:facade type="stateless"
 * 
 */
public abstract class LanguageBean implements EntityBean {

    /**
     * @throws CreateException 
     * @ejb.create-method
     */
    public LanguagePK ejbCreate(String election, String languageCode)
            throws CreateException {
        LanguagePK pk = new LanguagePK( election, languageCode );
        return pk;
    }

    /**
     * @ejb.pk-field
     * @ejb.pk
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="election"
     */
    public abstract String getElection();

    /**
     * @ejb.interface-method
     */
    public abstract void setElection(String name);

    /**
     * @ejb.pk-field
     * @ejb.pk
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="languagecode"
     */
    public abstract String getLanguageCode();

    /**
     * @ejb.interface-method
     */
    public abstract void setLanguageCode(String name);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="header"
     */
    public abstract String getHeader();

    /**
     * @ejb.interface-method
     */
    public abstract void setHeader(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="footer"
     */
    public abstract String getFooter();

    /**
     * @ejb.interface-method
     */
    public abstract void setFooter(String footer);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="wrongfirstpassword"
     */
    public abstract String getWrongFirstPassword();

    /**
     * @ejb.interface-method
     */
    public abstract void setWrongFirstPassword(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="wrongsecondpassword"
     */
    public abstract String getWrongSecondPassword();

    /**
     * @ejb.interface-method
     */
    public abstract void setWrongSecondPassword(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="novoterfound"
     */
    public abstract String getNoVoterFound();

    /**
     * @ejb.interface-method
     */
    public abstract void setNoVoterFound(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="successfulvoting"
     */
    public abstract String getSuccessfulVoting();

    /**
     * @ejb.interface-method
     */
    public abstract void setSuccessfulVoting(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="failedvoting"
     */
    public abstract String getFailedVoting();

    /**
     * @ejb.interface-method
     */
    public abstract void setFailedVoting(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="invalidnumber"
     */
    public abstract String getInvalidNumber();

    /**
     * @ejb.interface-method
     */
    public abstract void setInvalidNumber(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="gotohomepage"
     */
    public abstract String getGoToHomePage();

    /**
     * @ejb.interface-method
     */
    public abstract void setGoToHomePage(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="candidateheader"
     */
    public abstract String getCandidateHeader();

    /**
     * @ejb.interface-method
     */
    public abstract void setCandidateHeader(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="candidatefooter"
     */
    public abstract String getCandidateFooter();

    /**
     * @ejb.interface-method
     */
    public abstract void setCandidateFooter(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="invalidlogin"
     */
    public abstract String getInvalidLogin();

    /**
     * @ejb.interface-method
     */
    public abstract void setInvalidLogin(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="confirmchoice"
     */
    public abstract String getConfirmChoice();

    /**
     * @ejb.interface-method
     */
    public abstract void setConfirmChoice(String header);

    /**
     * @ejb.persistent-field
     * @ejb.interface-method
     * @ejb.persistence column-name="electionclosed"
     */
    public abstract String getElectionClosed();

    /**
     * @ejb.interface-method
     */
    public abstract void setElectionClosed(String header);

}
