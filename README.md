I’ve always found mathematics to be fascinating in how it appears everywhere in our world, but also how so many entities are built solely off of math which allows for advanced solutions and strategies to arise given these mathematical bases. Of course this occurs all around our world but especially in math-based games. Stemming from my love for and knowledge of mathematics, when I started to program when I entered college as a computer science student, I thoroughly enjoyed it and found myself to be proficient in it. Fast forward six months, I was bored during spring break and wanted to build a program as my first personal project to not only demonstrate my skills, but also further them, and I thought that building a program to play a game would be a good start.

I found the card game Blackjack to be a game with strategy straightforward enough for me to be able to develop a program for with my current programming knowledge, while also being complex enough for me to demonstrate my high levels of skill in algorithm development and probability theory as well as my knowledge of the Java programming language. Also, some variations of Blackjack allow for the player to have an advantage in comparison to the dealer, which was a good goal for me to shoot for.

A basic set of rules for Blackjack can be found here: https://bicyclecards.com/how-to-play/blackjack/ 

I chose to program a player for a Blackjack game with the following rules:
One deck shoe
Blackjack pays 3:2
Dealer stands on soft 17
No resplitting
Double on any two cards
Player can surrender after dealer checks for Blackjack
Shuffle after every hand

According to various online calculators of the win rate, the player will effectively win 50.06 percent of games, and using the algorithm that I developed from scratch, I managed to achieve that same win rate over the simulation of more than one billion games of Blackjack.


The Algorithm:

First, the program finds the probabilities of the dealer ending with each 17, 18, 19, 20, 21, or “busting”, or going over 21, while considering the current value of the dealer’s hand, how many of each differently-valued card remains in the shoe, and whether the dealer has an ace. The possibility of drawing every differently-valued card is considered, and if drawing certain cards would keep the dealer’s hand total at under 17, they would draw another card, which the program of course considers. The precise probabilities of drawing different cards at different points is also considered so that the probabilities of ending with each possible final total are as legitimate as possible and that they add up to equal 1.

Then, we find the probabilities of the player ending with every possible total (12, 13, 14, and so on until 21, and also over 21) if they were to “hit”, or choose to draw another card. These probabilities are of course based on the player’s current value of their hand, whether they have an ace and how many of each card remain to be drawn, but also the dealer’s probabilities of ending with different totals. If their total is less than 12, they obviously should draw another card since there is no chance of them going over 21 which would automatically lose them the game; and if it is greater than 18 (or greater than 16 and they don’t have an ace) they should not draw another card because the probability of busting is too high. However, in between these thresholds, we check to see if it is favorable to draw another card considering their updated hand value and the updated amount of each card remaining in the shoe. 

We also find the probability of the player winning if they “stand”, electing not to draw another card, by finding how likely it is that the dealer either busts or ends their hand with a value less than the player’s current value. In addition, we find the probability of the player winning if they were to hit by adding up the probabilities of all the possible end results in which the player’s hand value is greater than the dealer’s. Whether the player should hit or stand depends solely on whether their probability of winning while hitting is greater than or less than their probability of winning while standing.

Then, we look at whether the player should double the stakes of the game by calculating the probabilities of them ending with different totals if they are only allowed to receive one more card, as is the case when the player doubles. We then find the probability of the player winning with these probabilities, then finding the expected difference in games won between the player and dealer both while doubling and either hitting or standing, depending on the better decision. If two times the expected value while doubling is greater than the general expected value, then the player should double. Lastly, if the expected value while not doubling is less than -.5, the player will be advised to surrender the hand and not play it out, effectively losing half of the game.

Then, we look at if the player can “split” their hand into two separate ones, which is the case when their first two cards have the same value. We find the probabilities of the player ending with each possible different total using the same strategy as mentioned before, finding the expected value of playing one of the split hands and multiplying it by two, because there are two of these hands. This value is compared to the expected value of not splitting, and if splitting yields a higher expected value, then that is the choice that is made, and two hands are played out using the same strategy that will be touched on below.

If the player should hit, then they draw another card, and they will also double if that is what is advised. Their hand total will be updated with the value of the new card and then it will be checked to see if they should hit again, until it is found that they shouldn’t. 
At this point, the dealer will then play out their hand and through over 1.2 billion simulations of hands being played, the player wins at a rate of 50.06 percent in a game designed for them to win less than 50 percent of the time. Again, this is in accordance with the win rate according to Blackjack experts with the above-mentioned set of rules being used.


Other notes regarding design and efficiency:

At one point, I attempted to incorporate classes into this program while utilizing inheritance, but the construction of so many objects during one run of a game proved to be too detrimental to the runtime of the program, no longer running on my machine once around 15 games had been played.
On the contrary, sticking to the use of static methods throughout the entirety of the program was ideal, as all of the methods simply get run through without objects being present to have to call them. This design allows for the simulation of hundreds of millions of games, which admittedly does take a few hours; precisely, the simulation of one million games of Blackjack proved to cost roughly 140 seconds.
Lastly, a point in my design which could be improved to potentially increase the win rate by a few hundredths of a percent is, when a card is taken out of the shoe, updating the dealer’s probabilities of ending with their different final total possibilities. This would have the effect of making the later calls of the methods more precise. However, like the incorporation of classes, this was too computationally expensive and would not be worth the tradeoff in regards to efficiency, especially considering that an advantage for the player is already present.