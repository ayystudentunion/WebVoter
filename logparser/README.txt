
Tulossivugeneraattori WebVoterin STV-vaaleille

1) P‰ivit‰ ehdokkaat tiedostoon ehdokkaat.txt

2) Kopioi WebVoterin ‰‰ntenlaskennan loki nimelle webvoter.log.
   Helpoiten saa server.log:sta webvoter.log:n n‰in:
   grep SingleTransferrableVote server.log|colrm 1 76 > webvoter.log

3) Poista html-hakemistosta vanhat html-tiedostot (s‰‰st‰
   kuva- ja tyylitiedostot)

4) Aja logparser.pl

5) Julkaise html-hakemiston sis‰ltˆ

Ohjelman ajon p‰‰tteeksi luodaan kaksi lokia:

log.elected_per_vote.txt
   Listaa ehdokkaat, joiden l‰pimenoon kukin ‰‰ni on vaikuttanut.

log.remaining_vote_values.txt
   Listaa kustakin ‰‰nest‰ j‰ljell‰ olevan ‰‰nim‰‰r‰n.

