package blackjackPlayer;

/* This class consists of methods which will assist in making decisions during a Blackjack
 * in order to optimize a player's chances of winning. */

public class BlackjackPlayer {
	
	/* This method returns an array of the probabilities the dealer could finish with, in order by index,
	 * 17, 18, 19, 20, 21, and over 21 (busted). It takes in as parameters the dealer's current total, the
	 * array of cards left to draw from, the probability that this current call of the method occurs,
	 * whether the dealer has an ace, a number representing the dealer's first card (0 for 10, 1 for ace) 
	 * and the array which will store the probabilities that will then be returned, which, when this 
	 * method is originally called, will always be a new, empty array so that the numbers in the array
	 * add up to one when the method has fully ran through. 
	 * What this method will do is loop through all of the values of cards in the shoe, checking the value
	 * of the dealer's hand if they were to get a card with each value. If it is at least 17, the "probs"
	 * array will get added to it the probability that the dealer ends with whatever number they end with.
	 * If it is less than 17, the method will be called again with that sub-17 number as the argument for
	 * the "total" parameter, while arguing the probability that the previous card was pulled for the
	 * "prob" parameter, made effective by the "newProb" local variable always being multiplied by the
	 * argument for "prob". */
	public static double[] getDealerProbs(int dealer, int[] cardsLeft, 
			double prob, boolean acePresent, int originalNum, double[] probs) {
		
		// This value will be the index that corresponds to the card whose possibility of the dealer
		// having as their down card, we do not need to account for. As an example, if the dealer's first 
		// card had a value of 10, 0 would get argued for the originalNum parameter, setting newNum to 1, 
		// the index of aces. We know that the dealer's down card is not an ace because they peeked for 
		// Blackjack and if it was an ace, they would have gotten Blackjack and the point in the game 
		// where this method was called would not have been reached. Also, as seen below, this number is 
		// only relevant if it is 0 or 1, as Blackjack can only be achieved with a 10 or ace as the value
		// of the first card.
		int impossibleIdx = 1 - originalNum;

		// Stores the number of cards in the shoe that we know the dealer will not show or pull next.
		// This will be subtracted from the number of total cards which could next be shown by the dealer.
		int numsWillNotPull = (impossibleIdx == 0 || impossibleIdx == 1) ? cardsLeft[impossibleIdx] : 0;

		// This is the number of cards that the dealer could show, which is all of the cards left in
		// the shoe minus the number of cards we know the dealer will not show. When the dealer hits
		// again after showing their down card, all of the cards in the shoe need to be accounted for,
		// since it is being taken directly out of the shoe rather than being flipped over. Because of 
		// this, 2 is argued as originalNum when the dealer hits and this method is called recursively so
		// that numsWillNotPull will go to 0 and cardsThatCouldBeShown is equal to all of the cards left 
		// in the shoe.
		int cardsThatCouldBeShown = Blackjack.getSum(cardsLeft) - numsWillNotPull;
		
		// Loops through all of the differently valued cards for the sake of seeing the result if that card
		// is the next card dealt to the dealer.
		for (int idx = 0; idx < cardsLeft.length; idx++) {
			
			// If the dealer's first number was 10, we can assure that their down card is not ace because 
			// they peeked for Blackjack, and if it was an ace, they would have gotten Blackjack and this
			// method would not get called. The same goes for starting with an ace and then getting a 10 
			// as the second card. Essentially, if the dealer's first card was a 10, we do not need to 
			// account for the possibility of their second card being an ace, because that possibility 
			// does not exist. The same goes for starting with an ace and also having a 10.
			if ((originalNum == 0 && idx == 1) || (originalNum == 1 && idx == 0)) {
				continue;
			}
			
			// Sets whether an ace is present back to the initial value for the argument, as it can and
			// will be changed during the iterations
			boolean hasAce = acePresent;
			
			// Sets the dealer's current number back to the number which was the argument for the dealer 
			// parameter, as it can and will be changed during the iterations
			int newDealer = dealer;
			
			// Creates a new array of the remaining cards which is just a copy of the parameter argument, 
			// so that the array can be altered within this iteration of the loop but then set back to 
			// its initial state for the next iteration.
			int[] newArray = cardsLeft.clone();
			
			// Since the card being pulled corresponds to the current index in the loop, a card needs to 
			// be taken away, but only if there are any cards remaining at the index. If there aren't any
			// cards of the value which the current index corresponds to, the next card will be looked at
			// and this iteration will be terminated.
			if (newArray[idx] > 0) {
				newArray[idx]--;
			} else {
				continue;
			}
			
			// Will store the value of the card added to the dealer's hand during this iteration
			int currVal;
			
			// The card is worth 10 if the index is 0
			if (idx == 0) {
				currVal = 10;
			} else if (idx == 1 && !hasAce && newDealer + 11 < 22) {
				// If the card is an ace, the dealer does not yet have one, and adding 11 to the dealer's
				// current total would not put it over 21
				currVal = 11;
				hasAce = true;
				
				
			} else {
				// If the index is not 0 or 1, or the index is 1 but the ace cannot count as 11, the 
				// card holds a value which is the current index
				currVal = idx;
			}
			
			// Stores the dealer's current hand value during this sequence of cards
			int dealerTotal = newDealer + currVal;
			
			// If there is an ace which counts as 11 and the dealer's new total is over 21, 
			// the ace now counts as 1
			if (hasAce && dealerTotal > 21) {
				dealerTotal -= 10;
				hasAce = false;
			}
			
			// Essentially, this stores the probability that this exact point in the sequence of the 
			// dealer drawing cards is reached in regards to the values of cards the dealer drew and in
			// what order they did so. Gets the probability that a card with the value corresponding to 
			// the current index is the one shown, which is the number of cards of the current value 
			// divided by the cards the dealer could show, and multiplies this by the probability that 
			// the current call of the method took place, or the probability that the card drawn before
			// the current one was drawn.
			double newProb = (double) cardsLeft[idx] / cardsThatCouldBeShown * prob;
			
			
			// If the total will not reach 17, the method is called recursively for the new total
			if (dealerTotal < 17) {
				
				// Finds the probabilities of the dealer ending with different totals after showing the
				// card corresponding to the current index. Also argues the new, copied array, which had
				// removed from it the card the dealer showed and also argues the above mentioned 
				// probability. 2 is also argued for the originalNum for reasons explained above, but to
				// reiterate, the dealer's first unknown card we had to account for was already taken out
				// of the shoe, and we knew that it did not give the dealer Blackjack. But this call of 
				// the method is considering the possibilities of when the dealer directly draws a card
				// from the shoe, so there are no concerns regarding having Blackjack like there were 
				// with the original call of the method, and 2 being the argument will have no effect.
				getDealerProbs(dealerTotal, newArray, newProb, hasAce, 2, probs);
				
			} else if (dealerTotal < 22) {
				
				// If the dealer's total did not go over 21 but is at least 17, adds to the index that 
				// stores the probability of the dealer ending with the current total, the probability 
				// that this exact sequence of cards drawn is reached. 17 is subtracted from the total
				// because the 0 index stores the probability of the dealer ending with 17, 1 index 18,
				// etc. The probability of the dealer busting, or ending with over 21, is stored by the 
				// 5 index.
				probs[dealerTotal - 17] += newProb;
				
			} else {
				
				// If the dealer's hand went over 21; the 5 index of the "probs" array will store the 
				// probability of the dealer busting, and it will get added to it the probability that
				// the dealer showed and pulled the cards that they did, in that same order.
				probs[5] += newProb;
				
			}
			
		}
		
		return probs;
		
	}
	
	/* This method will return an array of the probabilities that the player ends with, in order, 12, 13,
	 * 14, up until 21, and then the last index is the probability of the player busting. Takes in 
	 * parameters of the player's current total, the array of cards left, the probability that the method
	 * is called, whether the player has an ace, the array of probabilities which will be altered to then 
	 * store the probabilities of the player ending with different totals, and the array of probabilities 
	 * that the dealer ends up on certain numbers.
	 * The algorithm used is similar as used in the "getDealerProbs" method, looking at all of the cards
	 * available to draw, calling the method again if the player would end with less than 12 and never
	 * calling the method again if their total is at least 19, or 17 without an ace. However, in between
	 * 12 and whichever upper threshold is used, we check if the player should hit again, recursively 
	 * calling this method if so. But if not, we add to the player's "probs" array the probability of
	 * them ending with whatever total it is. */
	public static double[] getPlayerProbs(int total, int[] cardsLeft, double prob, 
			boolean acePresent, double[] probs, double[] dealerProbs) {
		
		// Loops through all of the differently valued cards for the sake of seeing the result if that card
		// is the next card dealt to the player.
		for (int idx = 0; idx < cardsLeft.length; idx++) {
			
			// Sets whether an ace is present back to the initial value for the argument, as it can and
			// will be changed during the iterations
			boolean hasAce = acePresent;
			
			// Sets the dealer's current number back to the number which was the argument for the dealer 
			// parameter, as it can and will be changed during the iterations
			int newTotal = total;
			
			// Creates a new array of the remaining cards which is just a copy of the parameter argument,
			// so that the array can be altered within this iteration of the loop but then set back to
			// its initial state for the next iteration.
			int[] newArray = cardsLeft.clone();

			// Since the card being pulled corresponds to the current index in the loop, a card needs to
			// be taken away, but only if there are any cards remaining at the index. If there aren't any
			// cards of the value which the current index corresponds to, the next card will be looked at
			// and this iteration will be terminated.
			if (newArray[idx] > 0) {
				newArray[idx]--;
			} else {
				continue;
			}
			
			// Will store the value of the card added to the dealer's hand during this iteration
			int currVal;

			// The card is worth 10 if the index is 0
			if (idx == 0) {
				currVal = 10;
			} else if (idx == 1 && !hasAce && newTotal + 11 < 22) {
				// The 1 index corresponds to aces, so if the card is an ace, the player does not 
				// already have an ace, and adding 11 to the current value of the player's hand, the ace
				// counts as 11
				currVal = 11;
				hasAce = true;
				
			} else {
				// If the index is not 0 or 1, or the index is 1 but the ace cannot count as 11
				currVal = idx;
			}
			
			// Will store the updated value of the player's hand
			int playerTotal = newTotal + currVal;
			
			// If there is an ace which counts as 11 and the player's new total is over 21, the ace now 
			// counts as 1
			if (hasAce && playerTotal > 21) {
				playerTotal -= 10;
				hasAce = false;
			}
			
			// Essentially, this stores the probability that this exact point in the sequence of the 
			// dealer drawing cards is reached in regards to the values of cards the dealer drew and in
			// what order they did so. Gets the probability that a card with the value corresponding to 
			// the current index is the one shown, which is the number of cards of the current value 
			// divided by the cards the dealer could show, and multiplies this by the probability that 
			// the current call of the method took place, AKA the probability that the previous card was
			// pulled at the time that it was.
			double newProb = (double) cardsLeft[idx] / Blackjack.getSum(cardsLeft) * prob;
			
			// If the player's total will not reach 12, the method is called recursively for the updated
			// total
			if (playerTotal < 12) {
				
				getPlayerProbs(playerTotal, newArray, newProb, hasAce, probs, dealerProbs);
				
			} else if (playerTotal < 22) {
				
				// In all cases where the player's total is at least 19, or when they don't have an ace
				// and their total value is at least 17, electing not to hit is always the best option,
				// so we do not even bother to check for the decision and automatically add the probability
				// of this sequence to the player's array of probabilities
				if (playerTotal > 18 || (playerTotal > 16 && !hasAce)) {
					
					probs[playerTotal - 12] += newProb;
				
				// Checks to see if the player should hit based on the new total, the dealer's 
				// probabilities, whether there is an ace present, and the array of cards left
			    } else if ((boolean) shouldHitAux(playerTotal, dealerProbs, hasAce, newArray)[0]) {
					// If the player should hit again, this adds the probabilities of them ending with
			    	// different totals after hitting, to the probabilities-storing array
					getPlayerProbs(playerTotal, newArray, newProb, hasAce, probs, dealerProbs);
				} else {
					// If the player shouldn't hit, the probability of the current total, gets added to 
					// it the probability that the exact current sequence took place in regards to the
					// cards the player drew
					probs[playerTotal - 12] += newProb;

				}
			} else {
				
				// If the player's total went over 21 in this current sequence, the last index of their
				// array of probabilities gets added to it the probability of this current sequence of
				// cards being drawn occurring
				probs[10] += (double)cardsLeft[idx] / Blackjack.getSum(cardsLeft) * prob;
				
			}
		
		}
		
		return probs;
	
	}

	
	/* Determines whether the player should hit or stand in a current situation, as well as whether they 
	 * should double, their probability of winning while drawing another card, their probability of
	 * winning while not drawing another card, and whether they should surrender the hand.
     * Takes in parameters of the player's current total, the probabilities of the dealer ending with 
     * different valyes, whether the player has an ace, and the array of cards remaining. */
	public static Object[] shouldHit(int total, double[] dealerProbs, boolean acePresent, 
			int[] cardsLeft) {
		
		// Finds just whether the player should hit as well as their probabilities of winning if hitting
		// and winning if standing
		Object[] shouldHitAux = shouldHitAux(total, dealerProbs, acePresent, cardsLeft);
		
		// Stores the information from the call of shouldHitAux
		boolean shouldHit = (boolean) shouldHitAux[0];
		double winIfHit = (double) shouldHitAux[1];
		double winIfStand = (double) shouldHitAux[2];

		// Stores whether the player should double the stakes of the game
		boolean shouldDouble = false;

		// If the player chooses to double their bet, they cannot draw more than the one card given to
		// them after they make this decision. The getDoubledPlayerProbs method accounts for this and
		// this variable will store the probabilities of finishing with different values after doubling
		double[] doubledPlayerProbs = BlackjackPlayer.getDoubledPlayerProbs(total, acePresent, cardsLeft);
		
		// Stores the probability of the player winning if they choose to double
		double winIfHitOnce = BlackjackPlayer.playerWinProb(doubledPlayerProbs, dealerProbs);

		// These store the effective hypothetical amount of games won for different decisions
		double doubleEV = 2 * getExpectedValue(winIfHitOnce);
		double nonDoubleEV = getExpectedValue(shouldHit ? winIfHit : winIfStand);
		
		// If doubling is the best option
		if (doubleEV > nonDoubleEV) {
			// To align with the logic of the program playing the game, if the player should double, 
			// that also means that they should hit
			shouldHit = true;
			shouldDouble = true;
		}

		// Stores if the player should make the decision to surrender the current game, electing to
		// effectively lose half of the game rather than risking losing the whole game. Initialized as
		// false because thus far, no reason has been given for the player to surrender
		boolean shouldSurrender = false;
		
		// If the player us expected to lose more than half of the value of the game based on the current
		// circumstances, then giving up half of the game and surrendering is the choice that is made
		if (nonDoubleEV < -.5) {
			shouldSurrender = true;
		}
		
		// Stores the objects which will be returned
		Object[] returnArray = { shouldHit, shouldDouble, winIfHit, winIfStand, shouldSurrender };
		
		return returnArray;
		
	}
	
	
	/* This method determines whether the player should hit and draw another card, or stand and draw no
	 * more cards. This method very well could not exist and have its contents inside of the above 
	 * shouldHit method, but the getPlayerProbs method needs to find simply if the player should hit and
	 * nothing else, so calling shouldHit from the getPlayerProbs method ran lots of unnecessary code, 
	 * decreasing the program's efficiency. However, the shouldHit method also needs access to the double
	 * values found in this method, which is why the return type here is an array of objects.
	 * The algorithm used is rather straightforward; if the player's probability of winning while hitting
	 * is greater than their probability of winning while standing, then they should hit, and they should
	 * not hit otherwise. */
	private static Object[] shouldHitAux(int total, double[] dealerProbs, boolean acePresent, 
			int[] cardsLeft) {
		
		// Stores if the player should hit
		boolean shouldHit;
		
		// Stores the player's probability of winning if standing given their current total and the
		// dealer's probabilities
		double winIfStand = winIfStand(total, dealerProbs);
		
		// Stores the probabilities of the player ending with the different possibilities of final totals
		double[] playerProbs = BlackjackPlayer.getPlayerProbs(total, cardsLeft, 1.0, acePresent, 
				new double[11], dealerProbs);
		
		// Finds the probability of the player winning while hitting
		double winIfHit = BlackjackPlayer.playerWinProb(playerProbs, dealerProbs);
			
		// If the player is more likely to win the game by hitting than they are standing, then they
		// should hit. Otherwise, they shouldn't
		shouldHit = winIfHit > winIfStand;
		
		// Stores the array of elements to be returned
		Object[] returnArray = { shouldHit, winIfHit, winIfStand };
		
		return returnArray;
		
	}
	

	/* This method will find the probabilities of the player ending with different totals if they were to
	 * choose to double the value of the game. In that case, they would only be allowed to draw one more
	 * card after they made the decision to double, which is what differs this method from the
	 * getPlayerProbs method, which incorporates recursion and accounts for the possibility of hitting
	 * again being the most optimal decision in regards to finding the player's probabilities. Takes in
	 * parameters for the player's current total, whether they have an ace, and the array of remaining
	 * cards in the shoe.
	 * The process used here is rather simple due to the impossibility of hitting again. We simply add to
	 * the probability of ending with a certain total the probability that we draw a card that puts us at
	 * that total. */
	private static double[] getDoubledPlayerProbs(int total, boolean acePresent, int[] cardsLeft) {
		
		// Will store the probabilities
		double[] playerProbs = new double[11];
		
		// Loops through all of the card values in the shoe, as we will look at what happens if each of
		// them are drawn
		for (int i = 0; i < cardsLeft.length; i++) {
			
			// These need to be set back to their original parameter value at the beginning of each 
			// iteration, as these could get altered during the iterations
			int totalCopy = total;
			boolean aceCopy = acePresent;
			
			// Will store the value of the current card
			int currVal;
			
			// 0 index corresponds to cards with values of 10
			if (i == 0) {
				currVal = 10;
			} else if (i == 1) {
				// If there is no ace and adding 11 to our total would keep it at 21 or under, an ace
				// counts as 11. Has a value of 1 otherwise
				if (!aceCopy && totalCopy + 11 < 22) {
					currVal = 11;
					aceCopy = true;
				} else {
					currVal = 1;
				}
				
			} else {
				// Value of the card is equal to the index it is stored at
				currVal = i;
			}
			
			int newTotal = totalCopy + currVal;
			
			// If the total went over 21 but the player has an ace, the ace now counts as 1
			if (newTotal > 21 && aceCopy) {
				newTotal -= 10;
				aceCopy = false;
			}
			
			// Stores the probability that the current card was pulled
			double newProb = (double) cardsLeft[i] / Blackjack.getSum(cardsLeft);
			
			// Looks at different totals that the player could end with
			if (newTotal <= 12) {
				// The 0 index will store the probability that the player ends with 12 or less
				playerProbs[0] += newProb;
			} else if (newTotal < 22) {
				// Each index stores the probability that the player ends with 12 more than the index's 
				// value
				playerProbs[newTotal - 12] += newProb;
			} else {
				// The final index stores the probability of the player busting
				playerProbs[10] += newProb;
			}
			
		}
		
		return playerProbs;
		
	}
	
	
	/* Returns the probability of the player winning based on two parameters: the player's array of 
	 * probabilities for each final total, and the dealer's. 
	 * This will consider all of the pairs of player-dealer final values where the player's value is
	 * greater, and accumulate the probbabilities of all of these combinations occurring. Additionally,
	 * because the two players ending the game with the same values has the effect of each of them winning
	 * half of the game, we add half of the probability of them tying to the probability of the player
	 * winning. */
	public static double playerWinProb(double[] player, double[] dealer) {
		
		// The probability of the player winning is initialized as the probability of the dealer busting
		// minus the probability of the player also busting
        double playerWin = dealer[5] - (dealer[5] * player[10]);
		
        // Will store the probability of a push, or tie, taking place
		double pushProb = 0.0;
		
		// Loops through the arrays and adds to the push probability the likelihoods of the player and 
		// dealer having the same total. Since the first number in the player's array corresponds to the 
		// probability of them finishing with 12, 5 must be added to that in order for the indices to 
		// line up, considering the dealer's array starts at 17. Essentially, this adds up the 
		// probabilities of both players finishing with 17, 18, 19, 20, and 21.
		for (int i = 0; i < 5; i++) { 
			pushProb += dealer[i] * player[i + 5]; 
		}
		
		// If the two sides have the same number, neither one wins, so effectively, each side wins half 
		// of the hands that end in a push.
		playerWin += pushProb / 2;
		
		// Adds to the probability of the player winning the player ending with certain totals and the 
		// dealer ending with less
		playerWin +=
			// Player ends with 21; dealer ends with 17, 18, 19, or 20
			(player[9] * (dealer[1] + dealer[2] + dealer[3] + dealer[0])) +
			// Player ends with 20; dealer ends with 17, 18, or 19
			(player[8] * (dealer[2] + dealer[1] + dealer[0])) +
			// Player ends with 19; dealer ends with 17 or 18
		    (player[7] * (dealer[0] + dealer[1])) + 
		    // Player ends with 18; dealer ends with 17
			(player[6] * dealer[0]);
		
		return playerWin;
			
	}
	
	
	/* This method finds the probability of the player winning if they elect to stand and not draw another
	 * card, ending with their current total. That probability is based on parameters for the player's 
	 * total and the dealer's probabilities of ending with various values. 
	 * The player will win the game if the dealer busts or their current total is greater than the 
	 * dealer's. Also, because the player and dealer ending with the same total results in each of them
	 * effectively winning half of the game, we add half of the probability of them ending with the same
	 * total to the variable that stores the win probability.
	 * The dealer's 0 index corresponds to 17, 1 to 18, 2 to 19, 3 to 20, 4 to 21, and 5 to busting. */
	private static double winIfStand(int total, double[] dealerProbs) {
		
		// Will store the probability
		double winIfStand = 0;
		
		if (total < 17) {
			
			// The player will only win if the dealer busts
			winIfStand = dealerProbs[5]; 
			
		} else if (total == 17) {
			
			// The players will tie if the dealer has 17 and win if they bust
			winIfStand = (dealerProbs[0] / 2) + dealerProbs[5]; 
			
		} else if (total == 18) {
			
			// The players will tie if the dealer has 18 and win if they bust or have 17
			winIfStand = dealerProbs[0] + (dealerProbs[1] / 2) + dealerProbs[5];
			
		} else if (total == 19) {
			
			// The players will tie if the dealer has 19 and win if they bust or have 17 or 18
			winIfStand = dealerProbs[0] + dealerProbs[1] + (dealerProbs[2] / 2) + 
					dealerProbs[5];
			
		} else if (total == 20) {
			
			// The players will tie if the dealer has 20 and win if they bust or have 17, 18, or 19
			winIfStand = dealerProbs[0] + dealerProbs[1] + dealerProbs[2] + 
					(dealerProbs[3] / 2) + dealerProbs[5];
			
		} else if (total == 21) {
			
			// The players will tie if the dealer has 21 and win if they bust or have 17, 18, 19, or 20
			winIfStand = dealerProbs[0] + dealerProbs[1] + dealerProbs[2] + 
					dealerProbs[3] + (dealerProbs[4] / 2) + dealerProbs[5];
			
		}
		
		return winIfStand;
		
	}
	

	/* This method will simply return the expected difference in games won between the player and the 
	 * dealer. This probability is the only parameter for this method. */
	public static double getExpectedValue(double prob) {
		
		// Essentially takes the probability of the player winning and subtracts from it the possibility 
		// of the player losing, as this will be the expected value since losses cancels out wins
		return 2 * prob - 1;
		
	}


}