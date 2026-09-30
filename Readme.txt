Readme File

About/Instructions page
To access the about page, click the exclamation mark in the menu.
To access the instructions page for every game, click the question mark at the bottom right corner.

Players
Players are loaded from players.txt at startup and saved back after every action and on exit, so balances persist between runs.
The file is written as a leaderboard sorted by balance (Comparator: CompareBalance). In memory the list is sorted by name
(Comparable: Player.compareTo) so that Collections.binarySearch can look a player up by name.

Changes & Bugs
Originally, I was supposed to make Keno, but I changed it to Video Poker Instead.
Blackjack now detects a natural blackjack (pays 3:2, dealer blackjack loses, both is a push) and skips the player action.
Splitting is still not implemented (the background image has no split button).

Bug-fix branch notes
- Card ranks and values were off by one (a bogus "One" rank, no King); Baccarat values and Poker hand detection were wrong.
- Card images for the dealer up-card, Baccarat and Poker were drawn one card off.
- Ace-high straights / Royal Flush were never detected; Jacks-or-better used the wrong ranks.
- Blackjack: double down now doubles the hand bet, busting records a loss, bet dialogs can be cancelled.
- Baccarat: Tie bets pay on the Tie stake, Player/Banker bets push on a tie.
- Dice: dice can roll a 6, multiplier is 1 / probability (fair), under range is 7-36.
- Dragon Tower: cash out only works during a round and pays for the rows actually cleared.
- Mines: a tile cannot be counted twice, cash out returns the stake plus profit.
- Every game reports the result of the round and updates wins, losses, wagered and profit consistently.
