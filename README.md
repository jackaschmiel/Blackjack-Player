# Blackjack Optimization and Monte Carlo Simulation

### DISCLAIMER: 
This project is purely for academic purposes. Users are not advised to use this program to assist with gambling, as this algorithm does not guarantee financial gain with complete confidence. 


## Introduction


### Game Summary 

Blackjack is a popular card game commonly found in casinos where a “player” challenges a “dealer”, both of them attempting to get the total of their cards’ values as close to 21 as possible without exceeding it, which is otherwise known as “busting”. The player is first given a card, then the dealer is given a card that is visible to the player, also called their “up-card”. The player then gets a second card before the dealer gets another card that the player cannot see, known as the “down-card”. The dealer then checks for Blackjack, which is touched on in the “Assumed Rules” subsection, winning the game on the spot if they have it. From here, the player makes a decision: they can “stand” (not draw another card), “hit” (draw another card), “double” (elect to double the size of their initial wager once the first four cards have been dealt, but they receive exactly one more card), or “split”. Splitting can occur only when the player’s two cards have the same value; the player will play two separate hands both starting with one of their initial two cards, also placing their initial wager on the newfound second hand. These hands will both be played against the dealer’s singular hand. If the player’s total exceeds 21, they immediately lose that hand’s game and the wager associated. Otherwise, once the player elects to stand, the dealer will then keep receiving cards until their hand total exceeds 16 (if it goes over 21, then the player wins). If neither hand exceeds 21, whoever’s hand total is closer to 21 wins. If the player wins, they earn back their wager plus their wager size (winning a game with a wager of 1 yields a profit of 1), but lose their wager if the game is lost. If both entities have the same value, also called a “push”, the player takes back the initial wager as the game was a tie. 


### Card Values

All of the cards in a standard 52-card deck from two to ten simply have the values denoted on the card (a 2 is worth 2, a 4 is worth 4, etc.). Jacks, Queens, and Kings also have values of 10. Aces, on the other hand, start off with a value of 11, but if it is in a hand whose total exceeds 21, the value of the ace goes down to 1. Likewise, if a hand is hit on, but adding 11 to it would cause its total to exceed 21, the ace is only worth 1; for example, if the player has an 8 and 6 and chooses to hit and receives an ace, the new total of the hand is 15, whereas holding an ace and 7 would yield a value of 18.


### Assumed Rules

Now that the idea of the game has been established, we can outline the rules that were assumed in the making of this model.

Blackjack pays 3:2  
A “Blackjack” occurs when the values of the player’s initial two cards add up to 21, in which case the player gains a profit of 1.5 times their initial wager. This is only if the dealer does not also have a Blackjack, when a push would occur.

Dealer stands on soft 17  
In some Blackjack variations, if the dealer has an ace and 6 after the initial deal, they hit, contrasting with the general rule of the dealer never hitting if their hand total is 17 or larger. However, other variations have the dealer stand in this scenario, which is what our optimization model will be doing.

No resplitting  
Once the player elects to split their two initial cards, if one of their two hands receives a card of that same value, they are not allowed to split again. For example, if the player starts with a Queen and Jack and elects to split these, then receiving a King with the Queen, they cannot split the King and Queen.

Player can surrender after dealer checks for Blackjack  
Rather than standing, hitting, doubling, or splitting, the player can choose to “surrender” the game, conceding the game right then but only losing half of their initial wager.

Player can hit split aces  
Those familiar with Blackjack may be aware that under some rulesets, the player cannot hit split aces after receiving the second cards for each hand. Say a player gets two aces and chooses to split them. If one of the hands gets a 2, putting that hand’s total at 13, the player is allowed to hit, as would likely be the advised option, in contrast with some variations where the player must stop hitting on that hand because it contained a split ace.

Player can double on any two cards  
Some variations of the game only allow the player to double the size of their wager if they have certain hand totals (9, 10, 11, for example), but the simulation allows for doubling given any two cards in the player’s hand


## The Algorithm


Now that the rules have been established, we can start exploring the rigorous algorithm used to optimize decision-making. To start off, as in any Blackjack game, the player receives their first card, the dealer receives their up card, the player gets a second card, and the dealer then receives a second card which will be the down-card. We then set the hand totals of both entities accordingly as well as identify whether each entity has an ace, and check for Blackjack in the process. (Aside: in this variation of Blackjack, if the dealer’s up card is an ace or has a value of 10, they check the down card to see if Blackjack was attained.) The simulation is conceded if either entity has Blackjack; a push will occur if both sides have it. 

During our optimization, we use an array to store the number of cards remaining of each value. The array that will be used for this is the real array of cards left that is used objectively in the simulation, but it is cloned after we receive our second card, as we do not know what the dealer’s down-card is and therefore cannot factor that into our strategy; this cloned array will be used in the optimization methods.

From here is where the decision-making optimization begins, as the player will now have to make a decision based on their cards and the dealer’s card(s). We first identify the probabilities of the dealer ending with all of the possible dealer totals (17 through 21, or >21). To start, we find how many cards the dealer’s next one could be, or how many cards have not been shown; we then check to see if a card value, specifically 10 or ace, can be ruled out, as is the case when the dealer’s up-card is an ace or 10, respectively, because this would give them Blackjack, which they peeked for; this point in the game would not be reached if they did have Blackjack. We then use dynamic programming to find the probabilities, looping through the size-10 array storing the amount of cards remaining of each value and checking the dealer’s new total after getting each possible value; during each iteration, if it is less than 17, we recursively call the method for this total and the array of cards left, also indicating the probability of the card that was pulled in that iteration, being pulled. Otherwise, we add to the dealer’s array of probabilities at the index storing the current total, the probability of the sequence of cards being pulled in the current recursive steps, obviously excluding the dealer’s up-card because it is already known. During all of this, we track whether the dealer has an ace, originally counting it as 11 until that would push the dealer’s hand total above 21, when we reduce the ace’s value to 1. Of the same token, if the dealer drawing an ace worth 11 would put their total above 21, it only counts as 1.

We then run a very similar process to find the player’s probabilities of ending with all of their possible totals. The only difference for the player from the dealer is that if the player’s current hand total is inclusively between 12 and 16 (18 with an ace), we check to see if the player should hit again given that total. If they should hit, we recursively call the method for the updated total. We also call it recursively if the current hypothetical total is less than 12; if it is found that recursion shouldn’t take place, we just add to the array of probabilities at the current total’s index the probability of the sequence of cards being pulled in the current recursive steps, of course excluding the first two cards, as these are already known. For both arrays of probabilities, due to clever parameterization in the methods, the probabilities of ending with different totals add up to 1, abiding by The Law of Total Probability. 

Then, we find what decision the player should make from the position of their two cards against the dealer’s up-card, using these arrays of probabilities. It is first found the probability of the player winning if they stand, which is the probability of the dealer busting or ending with a hand total less than the player’s current one from their original cards. It is also found the probability of the player winning if they hit, which is found by running convolutions on the two vectors, those being the player’s and dealer’s probabilities; we just find the probability that the player ends with a hand total greater than the dealer’s. Whichever win probability is higher, hitting or standing, is the preliminary decision that should be made. 

But then, we check if the player should double their wager but only receive one more card and concede the opportunity to hit afterwards. We find the player’s array of hand total probabilities under this. Like the aforementioned procedure, we loop through the size-10 array that contains the number of cards left in the shoe of each value, but we never call it recursively and always immediately add to the array at any index, the probability of the player being dealt the card that puts their hand total at the value corresponding to that index. To reiterate, in the process of finding all of these probabilities, if the hand total is greater than 21, we add the current probability to the last index of the array, which represents the probability of busting. We then find the expected value of doubling and compare it to the expected value of the previous best decision. Lastly, we see if the most optimal expected value is less than -0.5, in which case we will surrender the hand to evade the possibility of losing our entire wager; we only lose half of it when surrendering.

Next, we see if we can split our hand, meaning the values of our two cards are the same. If we can, we find our probabilities of ending with all the possible different totals, using the above non-doubled strategy. We then find the probability of winning with these probabilities, again using a convolution (we call the same method that was used previously in the algorithm). It is determined whether we should split based on its expected value compared to the optimal expected value of not splitting. If we split our hand, we play both hands while putting another wager of the original size on the newfound second hand, and these hands are both played out as is described below.

If we don’t split, a decision is then made that corresponds to the most optimal decision that was previously found. If we hit, we of course receive a new card, and elect to not hit if our total is above 16 or 18 with an ace. Otherwise, we iteratively find our new array of hand total probabilities and henceforth evaluate what the best decision in this spot is, using the procedure elaborated on above; this keeps occurring until it is found that we should not hit. If we split, we hit on both immediately as the total of each hand will always be less than 12, and we always hit in these cases. But once each hand has two cards in it, we check if we should double by finding the doubled array of player hand total probabilities and evaluating our decision from there. This is done for both hands, and we play out both of them using the same procedure of continuing to hit until we bust or elect not to.

This concludes our decision-making, as once we bust or elect to stop hitting, the dealer then plays out their hand. Our optimization puts us in a favorable spot to win. Through over billions of Monte Carlo simulations, our total win rate is 50.06 percent, equating to a house edge of -0.12 percent, which has obviously been negated. 



## Methods



### BlackjackSimulation class



#### `public static void main (String[] args)`

Of course, this is the driver of the simulation, as main() is always what is first run in a Java program, and any other methods need to be called from main(). So, we call the method that will simulate games, create an object storing the results of the simulation, and then print out the results.


#### `public static SimulationResult simulateGames(int games, int decks, double reshuffleRatio)`

##### Parameters:
`int games` how many games the simulation will run  
`int decks` how many decks of cards the shoe will hold during this simulation  
`double reshuffleRatio` once the ratio of cards in the shoe to the total number of cards in the decks is used, the shoe is reshuffled  

This method will simply run a Blackjack game any amount of times, equal to the “games” parameter, using the given number of decks and reshuffling the shoe if the ratio of cards left in it to the original number of cards in it, dips below the reshuffleRatio parameter. It simulates the exact process of a Blackjack game, outlined in the Introduction section, and uses other methods in the BlackjackSimulation and BlackjackOptimization classes to implement our algorithm, touched on later, to make the most optimal decisions given the scenario. At the end of the simulation, the cumulative results of all of the games will be printed out, including the house edge, player edge (negation of house edge), effective win rate, and how fast the games were simulated, as well as how often each possible result of a single game occurred.  
This method represents a Monte Carlo simulation of a stochastic process. The stochasticity lies in the fact that the first three cards (the player’s two and the dealer’s up card) are all randomly found and how often each value appears as one of these three cards will be proportional to the magnitude of that value in the shoe. While the decision-making from that point on is deterministic, the element of randomness in each simulation’s initial state qualifies it as being a stochastic process, and our algorithm is designed to handle this stochasticity. We then run a Monte Carlo simulation on this, using random sampling to find the long-term results of our algorithm’s usage. Each individual input will be the three random cards, and our long-term outputs will be our win rate/house edge/player edge as well as the probabilities of each individual game outcome.  
Returns an object storing information about the simulation.


#### `public static double split(int num, int dealerFirst, int dealerSecond, boolean dealerHasAce, int[] cardsLeft, int[] trueCardsLeft)`

##### Parameters:
`int num` the value of each of the player’s two hands; the value of each of the two cards the player chose to split  
`int dealerFirst`, `int dealerSecond` the values of the dealer’s first and second cards
`boolean dealerHasAce` whether the dealer has an ace among their two cards  
`int[] cardsLeft` the array of cards left from the algorithm’s perspective, not considering the dealer’s down card; will be used to assist in decision-making throughout this method. Each index stores the number of cards left at the corresponding value, which is as follows: 0:10, 1:aces, 2:2, 3:3, …, 9:9  
`int[] trueCardsLeft` the real array of cards left, considering the dealer’s down card; is used when drawing cards throughout this method

This method will simulate a “split”, when the player’s two initial cards have the same value and they elect to split them into two separate hands while putting another one of their initial wager on the second hand, doubling the stakes of the game. It contains the exact same simulation process as simulateGames() does, except it runs both of the hands for the player before the dealer plays out their hand.
Returns the result of the split in terms of the player’s original wager.


#### `public static int hit(int[] remainingCards)`

##### Parameters:
`int[] remainingCards` the array of cards remaining that the player and dealer will randomly be dealt cards from  

This will get a random integer from 0 to cardsLeft - 1 (inclusive), and find the corresponding card value if all of the cards remaining were hypothetically laid out starting with 10s, then aces, 2s, 3s, and so on. That card is removed from the shoe and then its value is returned.


#### `public static int getSum(int[] arr)`

##### Parameters:
`int[] arr` the array of cards remaining in the shoe


This returns the sum of all the values in an integer array, this always being the size-10 array that stores the cards remaining of each value. Used for the purpose of finding the probability that the dealer or player ends with a certain number, as the return value of this method will be the denominator in such fractions throughout other methods in this class.




#### `private static int getVal(int randomInt, int[] cardsLeft)`


##### Parameters:
`int randomInt` the random integer that will be translated into the value of the card  
`int[] cardsLeft` the array of cards remaining in the shoe


This returns the value of a number as it corresponds to the amount of cards left in the shoe. Essentially, all of the remaining cards are lined up, starting with tens/jacks/queens/kings, then going to aces, then twos, and so on. This method will get the value of the card at the location corresponding to the randomInt parameter.




#### `public static void removeElements(int num, int[] cardsLeft)`


##### Parameters:
`int num` the value of the card to be removed from the shoe  
`int[] cardsLeft` the array of cards remaining in the shoe


This removes from the array of cards remaining, a specified card.




#### `private static void incArr(int[] outcomeTotals, double result)`


##### Parameters:
`int[] outcomeTotals` the array representing how many times every possible single-game result has occurred  
`double result` the result of the current game


This will increment the outcomeTotals array at the index that stores the result of the game that was just simulated; this is called in every simulation only once the result has been determined.






### `BlackjackOptimization` Class






#### `public static double[] getDealerProbs(int dealer, int[] cardsLeft, double prob, boolean acePresent, double[] probs)`


##### Parameters:
`int dealer` the dealer’s current hand total  
`int[] cardsLeft` the array of cards remaining in the shoe; size-10, each index storing how many cards of each value are left (0:10, 1:ace, 2:2, …, 9:9)  
`double prob` the probability that this current call to the method occurred, which is used in determining the probabilities that exact recursive sequences are reached  
`boolean acePresent` whether the dealer has an ace  
`double[] probs` will store the dealer’s probabilities of ending with different totals. An empty, size-6 array is always argued for this. The 0 index stores the probability of the dealer ending with 17, 1 index stores 18, and so on; the 5 index stores the bust probability


What this method will do is loop through all of the values of cards in the shoe, checking the value of the dealer's hand if they were to get a card with each value. If it is at least 17, the "probs" array will get added to it the probability that the dealer ends with whatever number they end with. If it is less than 17, the method will be called again recursively with that sub-17 number as the argument for the "total" parameter, while arguing the probability that the previous card was pulled for the "prob" parameter, made effective by the "totalProb" local variable always being multiplied by the argument for "prob".
Returns the originally argued, empty, size-6 array which will now store the dealer’s probabilities which add up to 1.




#### `public static double[] getPlayerProbs(int total, int[] cardsLeft, double prob, boolean acePresent, double[] probs, double[] dealerProbs)`


##### Parameters:
`int total` the player’s current hand total, which is the sum of the values of their current card  
`int[] cardsLeft` the array of cards remaining in the shoe; size-10, each index storing how many cards of each value are left  
`double prob` the probability that this current call to the method occurred, which is used in determining the probabilities that exact recursive sequences are reached  
`boolean acePresent` whether the player has an ace  
`double[] probs` will store the player’s probabilities of ending with different totals. An empty, size-11 array is always argued for this. The 0 index stores the probability of the player ending with 12 (or less), 1 index stores 13, and so on; the 10 index stores the bust probability  
`double[] dealerProbs` the dealer’s probabilities of ending with different totals; will be used in deciding whether a recursive call should be made  


The algorithm used is similar as used in the "getDealerProbs" method, looking at all of the cards available to draw, calling the method again if the player would end with less than 12 and never
calling the method again if their total is at least 19, or 17 without an ace. However, in between 12 and whichever upper threshold is used, we check if the player should hit again, recursively calling this method if so. But if not, we add to the player's "probs" array the probability of them ending with whatever total it is.
Returns the originally argued, empty, size-11 array which will now store the player’s probabilities which add up to 1.




#### `public static Object[] shouldHit(int total, double[] dealerProbs, boolean acePresent, int[] cardsLeft)`


##### Parameters:
`int total` the player’s current hand total  
`double[] dealerProbs` the probabilities of the dealer ending with all of their possible different totals  
`boolean acePresent` whether the player has an ace  
`int[] cardsLeft` : the array representing how many cards of each value are left in the shoe  


Determines whether the player should hit or stand in a current situation, as well as whether they should double, their probability of winning while drawing another card, their probability of winning while not drawing another card, and whether they should surrender the hand. We use the shouldHitAux() method for a lot of the work, but otherwise, we find the player’s array of probabilities if they double and their win probability in that case, and then the expected value which determines if that is the optimal choice. We also see if we should surrender, when even the most optimal expected value is less than -0.5.




#### `public static Object[] shouldHitAux(int total, double[] dealerProbs, boolean acePresent, int[] cardsLeft)`


##### Parameters: 
`int total` the player’s current hand total  
`double[] dealerProbs` the probabilities of the dealer ending with all of their possible different totals  
`boolean acePresent` whether the player has an ace  
`int[] cardsLeft` the array representing how many cards of each value are left in the shoe  


This method determines whether the player should hit and draw another card, or stand and draw no more cards. This method very well could not exist and have its contents inside of the above shouldHit method, but the getPlayerProbs method needs to find simply if the player should hit and nothing else, so calling shouldHit from the getPlayerProbs method ran lots of unnecessary code, decreasing the program's efficiency. However, the shouldHit method also needs access to the double values found in this method, which is why the return type here is an array of objects. The algorithm used is rather straightforward; if the player's probability of winning while hitting is greater than their probability of winning while standing, then they should hit, and they should not hit otherwise. These probabilities are found with the methods below. 
Returns whether the player should hit, their win probability if hitting, and their win probability if standing.




#### `private static double[] getDoubledPlayerProbs(int total, int[] cardsLeft)`


##### Parameters:
`int total` the player’s current hand total  
`int[] cardsLeft` the array representing how many cards of each value are left in the shoe  


This method will find the probabilities of the player ending with different totals if they were to choose to double the value of the game. In that case, they would only be allowed to draw one more card after they made the decision to double, which is what differs this method from the getPlayerProbs method, which incorporates recursion and accounts for the possibility of hitting
again being the most optimal decision in regards to finding the player's probabilities. Takes in
parameters for the player's current total, whether they have an ace, and the array of remaining
cards in the shoe. The process used here is rather simple due to the impossibility of hitting again. We simply add to the probability of ending with a certain total the probability that we draw a card that puts us at that total.
Returns the player’s probabilities of ending with all of the possible different hand totals, if they were to double.




#### `public static double playerWinProb(double[] player, double[] dealer)`


##### Parameters:
`double[] player` the player’s array of probabilities of ending with all of their possible different totals  
`double[] dealer` : the dealer’s array of probabilities of ending with all of their possible different totals  


Returns the probability of the player winning if they hit based on two parameters: the player's array of probabilities for each final total, and the dealer's. This will consider all of the pairs of player-dealer final values where the player's value is greater, and accumulate the probabilities of all of these combinations occurring; we perform convolutions of the two vectors to get this final probability. Additionally, because the two players ending the game with the same values has the effect of each of them winning half of the game, we add half of the probability of them tying to the probability of the player winning.




#### `public static double winIfStand(int total, double[] dealerProbs)`


##### Parameters: 
`int total` the player’s current hand total  
`double[] dealerProbs` the dealer’s array of probabilities of ending with all of their possible different totals  


This method finds the probability of the player winning if they elect to stand and not draw another card, ending with their current total. That probability is based on parameters for the player's total and the dealer's probabilities of ending with various values. The player will win the game if the dealer busts or their current total is greater than the dealer's. Also, because the player and dealer ending with the same total results in each of them effectively winning half of the game, we add half of the probability of them ending with the same total to the variable that stores the win probability; this is consistent with what we did in the playerWinProb() method, for when the player would elect to hit.




#### `public static double getExpectedValue(double prob)`


##### Parameters:
`double prob` a given probability  


This method will simply return the expected difference in games won between the player and the dealer, given a probability.



