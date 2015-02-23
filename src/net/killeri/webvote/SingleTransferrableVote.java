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

import java.util.*;

import org.apache.log4j.Logger;

/**
 * Calculates Finnish Single Transferrable Vote results. Unfortunately the logic
 * is quite complex, so to really understand this you better get a copy of
 * Kaarlo V?is?nen's document describing the STV system. The comments and
 * variable names refer to that document.
 * 
 * @author killer
 * 
 * @copyright Kalle Kivimaa
 */
public class SingleTransferrableVote {

    public static BigFraction MINUS_ONE = new BigFraction( -1, 1 );

    public static BigFraction ZERO = new BigFraction( 0, 1 );

    public static BigFraction ONE = new BigFraction( 1, 1 );

    /** Array of votes. Single vote per line, single candidate per column */
    protected int[][] m_votes;

    /** Placement counts. Single candidate per line, place per column */
    protected int[][] m_placeCount;

    protected int m_worstPlace;

    /** Dropped candidate numbers. Contains Integers. */
    public List m_droppedCandidates = new ArrayList();

    /** Remaining candidate numbers. Contains Integers. */
    public List m_remainingCandidates = new ArrayList();

    /** Array of vote remaining */
    protected BigFraction[] Aa;

    /** Vote counts per round */
    protected BigFraction[][] R;

    /** Total number of votes remaining */
    protected BigFraction V = ZERO;

    /** Randomizer */
    protected Random randomizer = new Random( 1 );

    /** Debug printing enabled */
    public boolean debug = false;

    private static Logger log = Logger
            .getLogger( SingleTransferrableVote.class );

    /**
     * Simple debugging printout method
     */
    private void printVotes() {
        for( int i = 0; i < m_votes.length; i++ ) {
            StringBuffer sb = new StringBuffer();
            sb.append( i + 1 );
            sb.append( ": " );
            for( int j = 0; j < m_votes[i].length; j++ ) {
                sb.append( m_votes[i][j] + " " );
            }
            log.debug( sb );
        }
    }

    /**
     * Adds a single vote to the vote table.
     * 
     * @param votes
     */
    public void addVotes(int[] votes) {
        if( m_votes != null ) {
            // Create a new longer array and copy the contents to it.
            int[][] newVotes = new int[m_votes.length + 1][m_votes[0].length];
            System.arraycopy( m_votes, 0, newVotes, 0, m_votes.length );
            newVotes[newVotes.length - 1] = votes;
            m_votes = newVotes;
        }
        // If this is the first vote, create the array.
        else {
            m_votes = new int[][] { votes };
        }
    }

    /**
     * Calculate the STV results based on the Finnish system.
     * 
     * @param p
     *            number of candidates to elect
     * @return array of Integers containing the elected candidates
     */
    public Object[] calculateResult(int toElect) {
        // Initialize variables
        debug = false;
        preprocess();
        calculateV();
        int K = m_votes[0].length;
        int E = K;
        int P = toElect;
        int round = 0;
        Integer[] result = new Integer[P];
        int elected = 0;

        for( int i = 0; i < m_votes.length; i++ ) {
            int maxCand = 0;
            for( int j = 0; j < m_votes[i].length; j++ ) {
                if( m_votes[i][j] > maxCand )
                    maxCand = m_votes[i][j];
            }
            for( int k = 1; k <= maxCand; k++ ) {
                boolean found = false;
                for( int j = 0; j < m_votes[i].length; j++ ) {
                    if( m_votes[i][j] == k )
                        found = true;
                }
                if( !found ) {
                    for( int j = 0; j < m_votes[i].length; j++ ) {
                        if( m_votes[i][j] > k ) {
                            if( debug )
                                log.debug( "Moving vote " + ( i + 1 )
                                        + " up at place " + ( j + 1 ) );
                            m_votes[i][j]--;
                        }
                    }
                }
            }
            if( debug && maxCand == 0 )
                log.debug( "Removing vote " + ( i + 1 ) );
        }

        log.info( "Starting election. V=" + V.asBigDecimal( 5, 1 )
                + ", K=" + K + ", P=" + P );
        while( elected < toElect && V.compareTo( ZERO ) > 0 ) {
            round++;
            int[] electedThisRound = new int[P];
            int roundElectCount = 0;

            // Step 4.2.1 Calculate base quantites for this round
            BigFraction Q = V.divide( P + 1 );
            R[round] = new BigFraction[m_votes[0].length];
            for( int i = 0; i < m_votes[0].length; i++ )
                R[round][i] = ZERO;
            log.info( "----------------------------------------------------" );
            log.info( "Round: " + round + ", Q=" + Q.asBigDecimal( 5, 1 )
                    + ", V=" + V.asBigDecimal( 5, 1 ) + ", P=" + P );
            if( debug )
                printVotes();

            // Step 4.2.6 here already
            // If (not) enough remaining candidates add them all and return
            if( m_remainingCandidates.size() + elected <= toElect ) {
                while( m_remainingCandidates.size() > 0 ) {
                    log.info( "Adding elected candidate " + m_remainingCandidates.get( 0 ));
                    result[elected++] = (Integer) m_remainingCandidates.get( 0 );
                    m_remainingCandidates.remove( 0 );
                }
                return result;
            }
            // Step 4.2.6 ends, continue with 4.2.1

            // Calculate vote sums for this round for each candidate
            for( int i = 0; i < m_votes.length; i++ ) {
                for( int j = 0; j < m_votes[0].length; j++ ) {
                    if( R[round][j] == null )
                        R[round][j] = ZERO;
                    if( m_votes[i][j] == 1 ) {
                        R[round][j] = R[round][j].add( getVoteForCandidate( i,
                                j ) );
                    }
                }
            }

            // Step 4.2.2
            // Find all candidates who should be elected in this round
            for( int j = 0; j < m_votes[0].length; j++ ) {
                if( m_remainingCandidates.contains( new Integer( j ) ) )
                    log.info( "Candidate " + ( j + 1 ) + " has "
                            + R[round][j].asBigDecimal( 5, 1 ) + " votes" );
                if( R[round][j].compareTo( Q ) > 0 ) {
                    log.info( "Candidate " + ( j + 1 )
                                + " provisionally elected" );
                    electedThisRound[roundElectCount++] = j;
                }
            }

            // If too many, something needs to be done - not really
            /*
             * if( roundElectCount + elected > P ) { if( debug )
             * System.out.println( "Too many elected this round, removing." ); //
             * Go thourgh round vote counts dropping the lowest until we have
             * exactly enough while( roundElectCount + elected != P ) {
             * electedThisRound = removeRoundWeakest( R, 1, round,
             * electedThisRound, roundElectCount ); if( electedThisRound.length !=
             * roundElectCount ) { roundElectCount = electedThisRound.length; }
             * else { electedThisRound = removeWorstPlacement( 1,
             * electedThisRound, roundElectCount ); } } }
             */

            // Process elected (sort, subtract their part of the votes, remove
            // their placements)
            for( int j = 0; j < roundElectCount; j++ ) {
                for( int k = j + 1; k < roundElectCount; k++ ) {
                    if( R[round][electedThisRound[j]]
                            .compareTo( R[round][electedThisRound[k]] ) < 0 ) {
                        int temp = electedThisRound[j];
                        electedThisRound[j] = electedThisRound[k];
                        electedThisRound[k] = temp;
                    }
                }
            }
            for( int j = 0; j < roundElectCount; j++ ) {
                result[elected++] = new Integer( electedThisRound[j] );
                P--;
                m_remainingCandidates
                        .remove( new Integer( electedThisRound[j] ) );
                // Step 4.2.3
                subtractCandidate( electedThisRound[j], Q,
                        R[round][electedThisRound[j]] );

                for( int i = 0; i < m_votes.length; i++ ) {
                    if( m_votes[i][electedThisRound[j]] == 1 ) {
                        int sum = 0;
                        for( int k = 0; k < m_votes[0].length; k++ ) {
                            if( m_votes[i][electedThisRound[j]] == m_votes[i][k] )
                                sum++;
                        }
                    }
                    m_votes[i][electedThisRound[j]] = 0;
                }
            }
            if( elected == toElect )
                break;

            // Step 4.2.4
            // If none are elected drop the lowest ranking candidate
            if( roundElectCount == 0 ) {
                BigFraction lowest = null;
                int tiedCount = 0;
                int[] tiedCandidates = new int[m_remainingCandidates.size()];
                for( int j = 0; j < m_votes[0].length; j++ ) {
                    if( m_remainingCandidates.contains( new Integer( j ) )
                            && ( lowest == null || R[round][j]
                                    .compareTo( lowest ) < 1 ) ) {
                        if( lowest != null
                                && R[round][j].compareTo( lowest ) == -1 )
                            tiedCount = 0;
                        lowest = R[round][j];
                        tiedCandidates[tiedCount++] = j;
                    }
                }
                if( tiedCount > 1 ) {
                    // Check previous rounds and original placements first
                    // before randomizing
                    List stillTied = checkPreviousRounds( tiedCandidates,
                            tiedCount, round );
                    if( stillTied.size() > 2 )
                        stillTied = checkOriginalPlacements( stillTied
                                .toArray(), 1 );
                    if( stillTied.size() > 2 ) {
                        int randPlace = randomizer.nextInt( tiedCount );
                        tiedCandidates[0] = tiedCandidates[randPlace];
                        log.debug( "Randomly dropping "
                                + ( tiedCandidates[0] + 1 ) );
                    } else {
                        tiedCandidates[0] = ( (Integer) stillTied.get( 0 ) )
                                .intValue();
                    }
                }
                for( int i = 0; i < m_votes.length; i++ ) {
                    m_votes[i][tiedCandidates[0]] = 0;
                }
                if( tiedCount == 1 )
                    log.info( "Dropping candidate "
                                    + ( tiedCandidates[0] + 1 ) );
                m_remainingCandidates.remove( new Integer( tiedCandidates[0] ) );
                m_droppedCandidates.add( new Integer( tiedCandidates[0] ) );
                E--;
            }

            // Step 4.2.5
            // Process the votes to the correct form after candidates have been
            // removed
            for( int i = 0; i < m_votes.length; i++ ) {
                int maxCand = 0;
                for( int j = 0; j < m_votes[i].length; j++ ) {
                    if( m_votes[i][j] > maxCand )
                        maxCand = m_votes[i][j];
                }
                int k = 1;
                for( boolean found = false; maxCand > 0 && !found; ) {
                    for( int j = 0; j < m_votes[i].length; j++ ) {
                        if( m_votes[i][j] == k )
                            found = true;
                    }
                    if( !found ) {
                        for( int j = 0; j < m_votes[i].length; j++ ) {
                            if( m_votes[i][j] > k ) {
                                if( debug )
                                    log.debug( "Moving vote " + ( i + 1 )
                                            + " up at place " + ( j + 1 ) );
                                m_votes[i][j]--;
                            }
                        }
                    }
                }
                if( debug && maxCand == 0 )
                    log.debug( "Removing vote " + ( i + 1 ) );
                else if( debug )
                    log.debug( "Processed vote " + ( i + 1 ) );
            }
            calculateV();
        }
        sortRemaining( round );
        return result;
    }

    protected void sortRemaining(int round) {
        Integer[] candidates = new Integer[m_remainingCandidates.size()];
        for( int i = 0; i < m_remainingCandidates.size(); i++ )
            candidates[i] = (Integer) m_remainingCandidates.get( i );
        for( int i = 0; i < candidates.length; i++ ) {
            for( int j = i + 1; j < candidates.length; j++ ) {
                if( R[round][candidates[i].intValue()]
                        .compareTo( R[round][candidates[j].intValue()] ) < 0 ) {
                    Integer temp = candidates[i];
                    candidates[i] = candidates[j];
                    candidates[j] = temp;
                }
                if( R[round][candidates[i].intValue()]
                        .compareTo( R[round][candidates[j].intValue()] ) == 0 ) {
                    boolean cont = true;
                    for( int k = round - 1; k > 0 && cont; k-- ) {
                        if( R[k][candidates[i].intValue()]
                                .compareTo( R[k][candidates[j].intValue()] ) < 0 ) {
                            Integer temp = candidates[i];
                            candidates[i] = candidates[j];
                            candidates[j] = temp;
                            cont = false;
                        } else if( R[k][candidates[i].intValue()]
                                .compareTo( R[k][candidates[j].intValue()] ) > 0 ) {
                            cont = false;
                        }
                    }
                    if( cont && randomizer.nextBoolean() ) {
                        Integer temp = candidates[i];
                        candidates[i] = candidates[j];
                        candidates[j] = temp;
                        cont = false;
                    }
                }
            }
        }
        m_remainingCandidates = Arrays.asList( candidates );
    }

    protected List checkPreviousRounds(int[] candidates, int count, int round) {
        log.debug( "Checking round " + round );
        if( round < 0 )
            throw new RuntimeException( "Invalid round: " + round );
        for( int i = 0; i < count; i++ ) {
            for( int j = i + 1; j < count; j++ ) {
                log.debug( "Comparing " + candidates[i] + " to "
                        + candidates[j] );
                if( R[round][candidates[i]].compareTo( R[round][candidates[j]] ) > 0 ) {
                    int temp = candidates[i];
                    candidates[i] = candidates[j];
                    candidates[j] = temp;
                }
            }
        }
        List returnList = new ArrayList();
        BigFraction last = null;
        for( int i = 0; i < count; i++ ) {
            if( last == null || R[round][candidates[i]].compareTo( last ) == 0 ) {
                returnList.add( new Integer( candidates[i] ) );
                last = R[round][candidates[i]];
            }
        }
        if( returnList.size() > 1 && round > 1 ) {
            int[] newCands = new int[returnList.size()];
            for( int i = 0; i < returnList.size(); i++ ) {
                newCands[i] = ( (Integer) returnList.get( i ) ).intValue();
            }
            return checkPreviousRounds( newCands, newCands.length, --round );
        }
        return returnList;
    }

    protected List checkOriginalPlacements(Object[] candidates, int place) {
        if( place < 1 || place > m_worstPlace )
            throw new RuntimeException( "Invalid place: " + place );
        for( int i = 0; i < candidates.length; i++ ) {
            for( int j = i + 1; j < candidates.length; j++ ) {
                if( m_placeCount[ ( (Integer) candidates[i] ).intValue()][place] > m_placeCount[ ( (Integer) candidates[j] )
                        .intValue()][place] ) {
                    Object temp = candidates[i];
                    candidates[i] = candidates[j];
                    candidates[j] = temp;
                }
            }
        }
        List returnList = new ArrayList();
        Integer last = null;
        for( int i = 0; i < candidates.length; i++ ) {
            if( last == null
                    || m_placeCount[ ( (Integer) candidates[i] ).intValue()][place] == last
                            .intValue() ) {
                returnList.add( candidates[i] );
                last = new Integer( m_placeCount[ ( (Integer) candidates[i] )
                        .intValue()][place] );
            }
        }
        if( returnList.size() > 1 && place < m_worstPlace ) {
            int[] newCands = new int[returnList.size()];
            for( int i = 0; i < returnList.size(); i++ ) {
                newCands[i] = ( (Integer) returnList.get( i ) ).intValue();
            }
            return checkPreviousRounds( newCands, newCands.length, place++ );
        }
        return returnList;
    }

    /**
     * Remove the weakest elected, recursively resolving ties
     */
    protected int[] removeRoundWeakest(BigFraction[][] Rs, int currentRound,
            int maxRounds, int[] elected, int electCount) {
        if( currentRound > maxRounds )
            return elected;
        int tiedCount = 0;
        BigFraction lowVotes = null;
        int[] tiedCandidates = new int[electCount];
        for( int j = 0; j < electCount; j++ ) {
            if( lowVotes != null
                    && Rs[currentRound][elected[j]].compareTo( lowVotes ) == 0 ) {
                tiedCandidates[tiedCount++] = j;
            }
            if( lowVotes == null
                    || Rs[currentRound][elected[j]].compareTo( lowVotes ) < 0 ) {
                lowVotes = Rs[currentRound][elected[j]];
                tiedCandidates[0] = j;
                tiedCount = 1;
            }
        }
        if( tiedCount > 1 )
            return removeRoundWeakest( Rs, currentRound++, maxRounds, elected,
                    electCount );
        int[] newElected = new int[electCount - 1];
        System.arraycopy( elected, 0, newElected, 0, tiedCandidates[0] );
        System.arraycopy( elected, tiedCandidates[0] + 1, newElected,
                tiedCandidates[0] + 1, electCount - tiedCandidates[0] - 1 );
        log.info( "Removing elected candidate "
                    + elected[tiedCandidates[0]] );
        return newElected;
    }

    protected int[] removeWorstPlacement(int currentPlace, int[] elected,
            int electCount) {
        if( currentPlace > m_placeCount[0].length )
            return elected;
        int tiedCount = 0;
        int lowest = -1;
        int[] tiedCandidates = new int[electCount];
        for( int j = 0; j < electCount; j++ ) {
            if( lowest > -1 && m_placeCount[elected[j]][currentPlace] == lowest ) {
                tiedCandidates[tiedCount++] = j;
            }
            if( lowest == -1 || m_placeCount[elected[j]][currentPlace] < lowest ) {
                lowest = m_placeCount[j][currentPlace];
                tiedCandidates[0] = j;
                tiedCount = 1;
            }
        }
        if( tiedCount > 1 )
            return removeWorstPlacement( currentPlace++, elected, electCount );
        int[] newElected = new int[electCount - 1];
        System.arraycopy( elected, 0, newElected, 0, tiedCandidates[0] );
        System.arraycopy( elected, tiedCandidates[0] + 1, newElected,
                tiedCandidates[0] + 1, electCount - tiedCandidates[0] - 1 );
        log.info( "Removing elected candidate "
                    + elected[tiedCandidates[0]] );
        return newElected;
    }

    /**
     * Calculate current V
     */
    protected void calculateV() {
        V = ZERO;
        for( int i = 0; i < m_votes.length; i++ ) {
            if( validVote( i ) ) {
                V = V.add( Aa[i] );
            }
        }
    }

    /**
     * Initialize arrays etc.
     */
    protected void preprocess() {
        m_placeCount = new int[m_votes[0].length + 1][m_votes[0].length + 1];
        Aa = new BigFraction[m_votes.length];
        R = new BigFraction[1000][m_votes[0].length];
        for( int i = 0; i < m_votes.length; i++ ) {
            Aa[i] = ONE;
        }
        for( int j = 0; j < m_votes[0].length; j++ ) {
            m_remainingCandidates.add( new Integer( j ) );
        }
        for( int i = 0; i < m_votes.length; i++ ) {
            for( int j = 0; j < m_votes[i].length; j++ ) {
                m_placeCount[j][m_votes[i][j]]++;
                if( m_worstPlace < m_votes[i][j] )
                    m_worstPlace = m_votes[i][j];
            }
        }
        log.debug( "Original placements" );
        for( int i = 1; i < m_placeCount.length; i++ ) {
            StringBuffer sb = new StringBuffer();
            for( int j = 1; j < m_placeCount[0].length; j++ )
                sb.append( m_placeCount[i][j] + " " );
            log.debug( "Candidate " + i + ": " + sb.toString() );
        }
    }

    /**
     * Calculate how much of the current vote belongs to the current candidate
     * 
     * @param i
     *            vote index
     * @param j
     *            candidate index
     */
    protected BigFraction getVoteForCandidate(int i, int j) {
        long sum = 0;
        for( int k = 0; k < m_votes[0].length; k++ ) {
            if( m_votes[i][k] == m_votes[i][j] )
                sum++;
        }
        return Aa[i].divide( sum );
    }

    /**
     * Remove candidate's part of the votes
     * 
     * @param j
     *            candidate index
     * @param Q
     *            current quorum
     * @param R
     *            candidate's vote sum for this round
     */
    protected void subtractCandidate(int j, BigFraction Q, BigFraction Rs) {
        if( ZERO.compareTo( Rs ) > -1 )
            return;
        BigFraction I = Q.divide( Rs );
        log.debug( "Subtracting candidate " + ( j + 1 ) + " with I="
                + I.asBigDecimal( 5, 1 ) );
        for( int k = 0; k < m_votes.length; k++ ) {
            StringBuffer db = new StringBuffer();
            if( validVote( k ) )
                db.append( "Vote: " + ( k + 1 ) + " Old="
                        + Aa[k].asBigDecimal( 5, 1 ) );
            if( m_votes[k][j] == 1 ) {
                long sum = 0;
                for( int l = 0; l < m_votes[0].length; l++ ) {
                    if( m_votes[k][l] == 1 )
                        sum++;
                }
                BigFraction sub = I.multiply( Aa[k].divide( sum ) );
                if( validVote( k ) )
                    db.append( " Subtracting=" + sub.asBigDecimal( 5, 1 ) );
                Aa[k] = Aa[k].subtract( sub );
            }
            if( validVote( k ) ) {
                db.append( " New=" + Aa[k].asBigDecimal( 5, 1 ) );
                log.debug( db );
            }
        }
    }

    protected boolean validVote(int k) {
        for( int j = 0; j < m_votes[k].length; j++ ) {
            if( m_votes[k][j] > 0 )
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SingleTransferrableVote stv = new SingleTransferrableVote();
        int candidates = Integer.parseInt( args[0] );
        int toElect = Integer.parseInt( args[1] );
        for( int i = 2; i < args.length; i += candidates ) {
            int[] votes = new int[candidates];
            for( int j = 0; j < candidates; j++ ) {
                votes[j] = Integer.parseInt( args[i + j] );
            }
            stv.addVotes( votes );
        }
        stv.debug = true;
        Object[] elected = stv.calculateResult( toElect );
        Object[] remaining = stv.m_remainingCandidates.toArray();
        for( int i = 0; i < remaining.length; i++ ) {
            for( int j = i + 1; j < remaining.length; j++ ) {
                if( stv.m_placeCount[ ( (Integer) remaining[i] ).intValue()][1] < stv.m_placeCount[ ( (Integer) remaining[j] )
                        .intValue()][1] ) {
                    Object temp = remaining[i];
                    remaining[i] = remaining[j];
                    remaining[j] = temp;
                }
            }
        }
        System.out
                .println( "----------------------------------------------------\nELECTED CANDIDATES" );
        for( int i = 0; i < elected.length; i++ )
            System.out.println( ( i + 1 ) + ". "
                    + ( ( (Integer) elected[i] ).intValue() + 1 ) );
        System.out
                .println( "----------------------------------------------------\nREMAINING CANDIDATES" );
        for( int i = 0; i < stv.m_remainingCandidates.size(); i++ )
            System.out.println( ( elected.length + i + 1 )
                    + ". "
                    + ( ( (Integer) stv.m_remainingCandidates.get( i ) )
                            .intValue() + 1 ) );
        System.out
                .println( "----------------------------------------------------\nDROPPED CANDIDATES" );
        for( int i = 0; i < stv.m_droppedCandidates.size(); i++ )
            System.out.println( ( elected.length
                    + stv.m_remainingCandidates.size() + i + 1 )
                    + ". "
                    + ( ( (Integer) stv.m_droppedCandidates
                            .get( stv.m_droppedCandidates.size() - i - 1 ) )
                            .intValue() + 1 ) );

    }
}
