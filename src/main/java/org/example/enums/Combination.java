package org.example.enums;

import lombok.RequiredArgsConstructor;
import org.example.entity.Impl.Card;

import java.util.*;
import java.util.function.Function;


@RequiredArgsConstructor
public enum Combination {
    FLASH_ROYAL(cards -> {
        Map<Suit, List<Card>> map = new HashMap<>();
        for (Card card : cards) {
            var list = map.getOrDefault(card.getSuit(), new ArrayList<>());
            list.add(card);
            map.put(card.getSuit(), list);
        }

        var optList = map.values()
                .stream()
                .filter(l -> l.size() > 4)
                .findFirst();

        if (optList.isEmpty()) return false;

        var list = optList.get();
        int prev = list.get(0).getNominal();

        if (prev != 10 || list.get(list.size() - 1).getNominal() != 14)
            return false;

        for (int i = 1; i < list.size(); i++) {
            int current = list.get(i).getNominal();
            if (current - prev != 1) return false;
            prev = current;
        }

        return true;
    }),
    STREET_FLASH(cards -> {
        Map<Suit, List<Card>> map = new HashMap<>();
        for (Card card : cards) {
            var list = map.getOrDefault(card.getSuit(), new ArrayList<>());
            list.add(card);
            map.put(card.getSuit(), list);
        }
        var optList = map.values()
                .stream()
                .filter(l -> l.size() > 4)
                .findFirst();

        if (optList.isEmpty()) return false;
        List<Card> parseCard = new ArrayList<>(optList.get());

        if (parseCard.size() > 4) {
            int i = 0;
            do {
                if (parseCard.get(i).getNominal() + 1 == parseCard.get(i + 1).getNominal() &&
                        parseCard.get(i + 1).getNominal() + 1 == parseCard.get(i + 2).getNominal() &&
                        parseCard.get(i + 2).getNominal() + 1 == parseCard.get(i + 3).getNominal() &&
                        parseCard.get(i + 3).getNominal() + 1 == parseCard.get(i + 4).getNominal()) return true;
                i++;

            } while (i < parseCard.size() - 4);
        }
        return false;
    }),
    BOB(cards -> {
        for (int i = 0; i < cards.length - 3; i++) {
            if (cards[i].getNominal() == cards[i + 1].getNominal() && cards[i + 1].getNominal() == cards[i + 2].getNominal() && cards[i + 2].getNominal() == cards[i + 3].getNominal()) {
                Player player = null;
                for (Card card : cards) {
                    if (card.isFirstPlayer()) {
                        player = Player.PLAYER_ONE;
                        break;
                    }
                    if (card.isSecondPlayer()) {
                        player = Player.PLAYER_TWO;
                        break;
                    }
                }
                for (int j = i; j < i + 4; j++) {
                    if (player == Player.PLAYER_ONE) {
                        cards[j].setInComboFirstPlayer(true);
                    } else {
                        cards[j].setInComboSecondPlayer(true);
                    }
                }
                return true;
            }
        }
        return false;
    }),
    FULL_HOUSE(cards -> {
        boolean pair = false;
        boolean set = false;
        int num = 0;
        Player player = null;
        for (Card card : cards) {
            if (card.isFirstPlayer()) {
                player = Player.PLAYER_ONE;
                break;
            }
            if (card.isSecondPlayer()) {
                player = Player.PLAYER_TWO;
                break;
            }
        }
        for (int i = 0; i < cards.length - 2; i++) {
            if (cards[i].getNominal() == cards[i + 1].getNominal() && cards[i + 1].getNominal() == cards[i + 2].getNominal()) {
                for (int j = i; j < i + 3; j++) {
                    if (player == Player.PLAYER_ONE) {
                        cards[j].setInComboFirstPlayer(true);
                    } else {
                        cards[j].setInComboSecondPlayer(true);
                    }
                }
                set = true;
                num = i;
                break;
            }
        }
        if (set) {
            Card[] cards1 = new Card[4];
            for (int i = 0, j = 0; i < 4; i++, j++) {
                if (j == num || j == num + 1 || j == num + 2) {
                    i--;
                } else {
                    cards1[i] = cards[j];
                }
            }
            Arrays.sort(cards1); // Они конечно и так отсортированны, но на всякий проверять не было времени
            for (int i = 0; i < 4 - 1; i++) {
                if (cards1[i].getNominal() == cards1[i + 1].getNominal()) {
                    for (int j = i; j < i + 2; j++) {
                        if (player == Player.PLAYER_ONE) {
                            cards[j].setInComboFirstPlayer(true);
                        } else {
                            cards[j].setInComboSecondPlayer(true);
                        }
                    }
                    pair = true;
                    break;
                }
            }
        }
        return pair;
    }),
    FLASH(cards -> {

        int c = 0, d = 0, h = 0, s = 0;
        for (int i = 0; i < cards.length; i++) {
            switch (cards[i].getSuit()) {
                case CLUBS:
                    c++;

                    break;
                case DIAMONDS:
                    d++;

                    break;
                case HEARTS:
                    h++;

                    break;
                case SPADES:
                    s++;

                    break;
            }
        }
        return c > 4 || d > 4 || h > 4 || s > 4;
    }),
    STREET(cards -> {
        Player player = null;
        for (Card card : cards) {
            if (card.isFirstPlayer()) {
                player = Player.PLAYER_ONE;
                break;
            }
            if (card.isSecondPlayer()) {
                player = Player.PLAYER_TWO;
                break;
            }
        }
        List<Card> card = new ArrayList<>();
        card.add(cards[0]);
        int iter;
        for (int i = 0; i < cards.length - 1; i++) {
            iter = i + 1;
            for (int j = 1; j < cards.length - i; j++) {
                if (cards[i].getNominal() + 1 == cards[iter].getNominal()) {
                    if (!card.contains(cards[i])) card.add(cards[i]);
                    card.add(cards[iter]);


                }
                iter++;
            }

        }
        if (card.size() > 4) {
            int i = 0;
            do {
                if (card.get(i).getNominal() + 1 == card.get(i + 1).getNominal() &&
                        card.get(i + 1).getNominal() + 1 == card.get(i + 2).getNominal() &&
                        card.get(i + 2).getNominal() + 1 == card.get(i + 3).getNominal() &&
                        card.get(i + 3).getNominal() + 1 == card.get(i + 4).getNominal()) {
                    for (int j = i; j < i + 5; j++) {
                        if (player == Player.PLAYER_ONE) {
                            cards[j].setInComboFirstPlayer(true);
                        } else {
                            cards[j].setInComboSecondPlayer(true);
                        }
                    }


                    return true;
                }
                i++;

            } while (i < card.size() - 4);


        }

        return false;
    }),
    SET(cards -> {
        for (int i = 0; i < cards.length - 2; i++) {
            if (cards[i].getNominal() == cards[i + 1].getNominal() && cards[i + 1].getNominal() == cards[i + 2].getNominal()) {
                Player player = null;
                for (Card card : cards) {
                    if (card.isFirstPlayer()) {
                        player = Player.PLAYER_ONE;
                        break;
                    }
                    if (card.isSecondPlayer()) {
                        player = Player.PLAYER_TWO;
                        break;
                    }
                }
                for (int j = i; j < i + 3; j++) {
                    if (player == Player.PLAYER_ONE) {
                        cards[j].setInComboFirstPlayer(true);
                    } else {
                        cards[j].setInComboSecondPlayer(true);
                    }
                }
                return true;
            }
        }
        return false;
    }),
    TWO_PAIR(cards -> {
        boolean pair = false;
        boolean result = false;
        Player player = null;
        for (Card card : cards) {
            if (card.isFirstPlayer()) {
                player = Player.PLAYER_ONE;
                break;
            }
            if (card.isSecondPlayer()) {
                player = Player.PLAYER_TWO;
                break;
            }
        }
        for (int i = 0; i < cards.length - 1; i++) {
            if (cards[i].getNominal() == cards[i + 1].getNominal() && !pair) {
                if (player == Player.PLAYER_ONE) {
                    cards[i].setInComboFirstPlayer(true);
                    cards[i + 1].setInComboFirstPlayer(true);
                } else {
                    cards[i].setInComboSecondPlayer(true);
                    cards[i + 1].setInComboSecondPlayer(true);
                }
                pair = true;
                if (i != 5) i++;
                else continue;
            }
            if (cards[i].getNominal() == cards[i + 1].getNominal() && pair) {
                if (player == Player.PLAYER_ONE) {
                    cards[i].setInComboFirstPlayer(true);
                    cards[i + 1].setInComboFirstPlayer(true);
                } else {
                    cards[i].setInComboSecondPlayer(true);
                    cards[i + 1].setInComboSecondPlayer(true);
                }
                result = true;
            }
        }
        return result;
    }),
    PAIR(cards -> {
        Player player = null;
        for (Card card : cards) {
            if (card.isFirstPlayer()) {
                player = Player.PLAYER_ONE;
                break;
            }
            if (card.isSecondPlayer()) {
                player = Player.PLAYER_TWO;
                break;
            }
        }
        for (int i = 0; i < cards.length - 1; i++) {
            if (cards[i].getNominal() == cards[i + 1].getNominal()) {
                if (player == Player.PLAYER_ONE) {
                    cards[i].setInComboFirstPlayer(true);
                    cards[i + 1].setInComboFirstPlayer(true);
                } else {
                    cards[i].setInComboSecondPlayer(true);
                    cards[i + 1].setInComboSecondPlayer(true);
                }
                return true;
            }
        }
        return false;
    }),
    HIGH_CARD(cards -> false);

    private final Function<Card[], Boolean> verifier;

    public boolean verify(Card[] cards) {
        return verifier.apply(cards);
    }
}
