package com.example.models.gameModels;
import javax.persistence.*;
import java.util.*;


@Entity
public class Game {

    public Game(){aPlayers = null; aVPsToWin = 10;}

    public enum GamePhase{
        SetupRoundOne,SetupRoundTwo, TurnFirstPhase, TurnDiceRolled, TurnSecondPhase, Completed
    }

    public enum DiceNumber{
        One, Two, Three, Four, Five, Six;
        public int add(DiceNumber pNumber){
            return pNumber.ordinal() + ordinal() + 2;
        }
        public int toInt(){
            return ordinal() + 1;
        }
    }

    public enum EventType{
        Barbarian, Trade, Politics, Science
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long gameid;

    @OneToMany
    private final List<Player> aPlayers;

    private final int aVPsToWin;

    @OneToOne
    private Board aBoard;

    private GamePhase aPhase;
    private int aBarbarianPosition;
    private int aArmyStrength;
    private int aBarbarianStrength;
    private DiceNumber aRedDice;
    private DiceNumber aYellowDice;
    private EventType aEventDice;
    private int aTurnCounter;

    public Game(int pVPsToWin, List<Player> pPlayers, Board pBoard){
        this.gameid = gameid;
        aBoard = pBoard;
        aVPsToWin = pVPsToWin;
        aBarbarianPosition = 6;
        aPlayers = pPlayers;
        aPhase = GamePhase.SetupRoundOne;
        aArmyStrength = 0;
        aBarbarianStrength = 0;
        aTurnCounter = 1;
    }

    public void setPhase(GamePhase pPhase){
        aPhase = pPhase;
    }

    public void setRedDice(DiceNumber pRedDice){
        aRedDice = pRedDice;
    }

    public void setYellowDice(DiceNumber pYellowDice){
        aYellowDice = pYellowDice;
    }

    public void setEventDice(EventType pEventDice){
        aEventDice = pEventDice;
    }

    public void setBarbarianStrength(int pStrength){
        aBarbarianStrength = pStrength;
    }

    public void setArmyStrength(int pStrength){
        aArmyStrength = pStrength;
    }

    public void updateBarbarianPosition(){
        aBarbarianPosition--;
    }

    public void resetBarbarianPosition(){
        aBarbarianPosition = 6;
    }

    public void updateTurn(){
        aTurnCounter++;
    }

    public Board getBoard(){
        return aBoard;
    }

    public void setBoard(Board pBoard) {aBoard = pBoard; }

    public List<Player> getPlayers(){
        return aPlayers;
    }

    public EventType getEventDice(){
        return aEventDice;
    }

    public DiceNumber getRedDice(){
        return aRedDice;
    }

    public DiceNumber getYellowDice(){
        return aYellowDice;
    }

    public int getBarbarianPosition(){
        return aBarbarianPosition;
    }

    public int getBarbarianStrength(){
        return aBarbarianStrength;
    }

    public int getArmyStrength(){
        return aArmyStrength;
    }

    public GamePhase getPhase(){
        return aPhase;
    }

    public int getTurnCounter(){
        return aTurnCounter;
    }
}
