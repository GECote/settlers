package com.example.models.gameModels;
import com.example.repositories.GameRepository;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameManager {

    private  Game aGame;

    @Autowired
    private  GameRepository gameRepository;


    public  ArrayList<Player> createPlayers(ArrayList<String> pPlayerNames) {
        ArrayList<String> aPlayerColors = new ArrayList<>();
        ArrayList<String> aPlayerNames = new ArrayList<>();
        ArrayList<Player> aPlayers = new ArrayList<>();

        aPlayerColors.add("red");
        aPlayerColors.add("yellow");
        aPlayerColors.add("green");
        aPlayerColors.add("blue");
        aPlayerNames = pPlayerNames;

        for (int i = 0; i < aPlayerNames.size(); i++) {
            aPlayers.add(new Player(aPlayerNames.get(i), aPlayerColors.get(i), i));
        }
        return aPlayers;
    }

    public  void createGame(int pVPsToWin, ArrayList<String> playerNames) {
        List<Player> aPlayers = createPlayers(playerNames);
        Board aBoard = new Board();
        Game game = new Game( pVPsToWin, aPlayers, aBoard);
        aGame = game;
    }

    public  void saveGame(){
        System.out.println(aGame);
        gameRepository.save(aGame);
    }


    public  Game getGame(){
        return aGame;
    }

    public void rollDice(int pYellow, int pRed, int pEvent){
        aGame.setPhase(Game.GamePhase.TurnFirstPhase);
        aGame.setRedDice(Game.DiceNumber.values()[pRed]);
        aGame.setYellowDice(Game.DiceNumber.values()[pYellow]);
        int eventIndex = pEvent;
        switch (eventIndex){
            case 1:
                aGame.setEventDice(Game.EventType.values()[0]);
            case 2:
                aGame.setEventDice(Game.EventType.values()[0]);;
            case 3:
                aGame.setEventDice(Game.EventType.values()[0]);
            case 4:
                aGame.setEventDice(Game.EventType.values()[1]);
            case 5:
                aGame.setEventDice(Game.EventType.values()[2]);
            case 6:
                aGame.setEventDice(Game.EventType.values()[3]);
        }
        checkBarbarian();
        checkDice();
        aGame.setPhase(Game.GamePhase.TurnDiceRolled);
    }

    private void checkBarbarian(){
        assert (aGame.getTurnCounter() > 2);
        if (aGame.getEventDice() == Game.EventType.Barbarian){
            advanceBarbPosition();
        }
    }

    private void advanceBarbPosition(){
        assert aGame.getBarbarianPosition() > 0;
        aGame.updateBarbarianPosition();
        if (aGame.getBarbarianPosition()== 0){
            barbarianAttack();
            resetBarbPosition();
        }
    }

    private void barbarianAttack(){
        assert (aGame.getBarbarianPosition() == 0);
        calculateStrengths();
        if (aGame.getBarbarianStrength() > aGame.getArmyStrength()){
            defenseLoss();
        }
        defenseVictory();
    }

    private void calculateStrengths(){
        aGame.setBarbarianStrength(0);
        aGame.setArmyStrength(0);
        for (Map.Entry<String, Intersection> intersection : aGame.getBoard().getIntersections().entrySet()){
            if (intersection.getValue().getOccupancyFlag()) {
                if (intersection.getValue().getBuilding() != null){
                    aGame.setBarbarianStrength(aGame.getBarbarianStrength() + 1);
                }
                else if (intersection.getValue().getKnight() != null){
                    if ((intersection.getValue().getKnight().getState())){
                        aGame.setArmyStrength(aGame.getArmyStrength() + (intersection.getValue().getKnight()).getStrength());
                    }
                }
            }
        }
    }

    private void resetBarbPosition(){
        aGame.resetBarbarianPosition();
    }


    private void defenseVictory(){
        //TODO
    }

    private void defenseLoss(){
        //TODO
    }


    private void checkDice() {
        int numberRolled = aGame.getRedDice().add(aGame.getYellowDice());
        ArrayList<LandHex> tempLandHexes = aGame.getBoard().getLandHexes().get(numberRolled);
        for (LandHex hex : tempLandHexes) {
            List<Intersection> tempIntersections = hex.getIntersectionNeighbours();
            for (Intersection intersection : tempIntersections) {
                if (intersection.getOccupancyFlag()) {
                    Player owner = getPayee(intersection);
                    boolean isCity = checkIsCity(intersection);
                    payout(owner, hex.getTerrainType(), isCity);
                }
            }
        }
    }

    private Player getPayee(Intersection pIntersection){
        assert (pIntersection.getBuilding() != null);
        return pIntersection.getBuilding().getOwner();
    }

    private boolean checkIsCity(Intersection pIntersection){
        return (pIntersection.getBuilding() != null && pIntersection.getBuilding().isCity());
    }

    private void payout(Player pOwner, TerrainType pTerrainType, boolean isCity){
        if (pTerrainType == TerrainType.GoldMine){
            pOwner.addGold();
        }
        else {
            StealableCard.Resource resource = pTerrainType.giveResource();
            pOwner.addResource(resource, 1);
        }

        if (isCity) {
            StealableCard.Resource resource = pTerrainType.giveResource();
            if (pTerrainType == TerrainType.Hills || pTerrainType == TerrainType.Fields){
                pOwner.addResource(resource, 1);
            }
            else if (pTerrainType == TerrainType.GoldMine){
                pOwner.addGold();
            }
            else{
                //if (aGame.getEventDice().equals(pOwner.)) TODO -- CHECK PLAYERS COMMODITY UPGRADE LVL
                StealableCard.Commodity commodity = pTerrainType.giveCommodity();
                pOwner.addCommodity(commodity, 1);
            }
        }
    }

    public void setupPayout(){
        for (Map.Entry<String,Intersection> intersection : aGame.getBoard().getIntersections().entrySet()){
            if (intersection.getValue().getBuilding() != null && intersection.getValue().getBuilding().isCity()){
                Player owner = intersection.getValue().getBuilding().getOwner();
                Intersection cityLocation = intersection.getValue();
                for(Hex hex : cityLocation.getHexNeighbours()){
                    TerrainType pTerrainType = hex.getTerrainType();
                    StealableCard.Resource resource = pTerrainType.giveResource();
                    owner.addResource(resource, 1);
                }
            }
        }
    }

    /*
    FUNCTIONS TO PAY FOR UNITS
     */

    private void paySettlement(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.SHEEP, 1);
        pPlayer.removeResource(StealableCard.Resource.WHEAT, 1);
        pPlayer.removeResource(StealableCard.Resource.BRICK, 1);
        pPlayer.removeResource(StealableCard.Resource.WOOD, 1);
    }

    private void payCity(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.WHEAT, 2);
        pPlayer.removeResource(StealableCard.Resource.ORE, 3);
    }

    private void payRoad(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.BRICK, 1);
        pPlayer.removeResource(StealableCard.Resource.WOOD, 1);
    }

    public void payShip(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.SHEEP, 1);
        pPlayer.removeResource(StealableCard.Resource.WOOD, 1);
    }

    /*
    FUNCTIONS TO PLACE UNITS
     */

    public void placeSettlement(Player pPlayer, Intersection pIntersection){
        OwnedBuilding settlement = pPlayer.removeBuilding(Unit.Building.SETTLEMENT);
        pIntersection.setBuilding(settlement);
    }

    public void placeCity(Player pPlayer, Intersection pIntersection){
        OwnedBuilding city = pPlayer.removeBuilding(Unit.Building.CITY);
        pPlayer.addBuilding(Unit.Building.SETTLEMENT);
        pIntersection.setBuilding(city);
    }

    public void placeRoad(Player pPlayer, Edge pEdge){
        OwnedTransport road = pPlayer.removeTransport(Unit.Transport.ROAD);
        pEdge.setTransport(road);
    }

    public void placeShip(Player pPlayer, Edge pEdge){
        OwnedTransport ship = pPlayer.removeTransport(Unit.Transport.ROAD);
        pEdge.setTransport(ship);
    }

    /*
    FUNCTIONS TO CHECK IF A PLAYER CAN BUY UNIT
     */

    public boolean checkBuySettlement(Player pPlayer) {
        if (pPlayer.getResourceCards().get(StealableCard.Resource.SHEEP) == 0 ||
                pPlayer.getResourceCards().get(StealableCard.Resource.ORE) == 0 ||
                pPlayer.getResourceCards().get(StealableCard.Resource.WOOD) == 0 ||
                pPlayer.getResourceCards().get(StealableCard.Resource.BRICK) == 0){
            return false;
        }
        return true;
    }

    private boolean checkBuyCity(Player pPlayer){
        if (pPlayer.getResourceCards().get(StealableCard.Resource.ORE) < 3 || pPlayer.getResourceCards().get(StealableCard.Resource.WHEAT) < 2){
            return false;
        }
        return true;
    }

    private boolean checkBuyRoad(Player pPlayer){
        if (pPlayer.getResourceCards().get(StealableCard.Resource.WOOD) == 0 || pPlayer.getResourceCards().get(StealableCard.Resource.BRICK) == 0){
            return false;
        }
        return true;
    }

    public boolean checkBuyShip(Player pPlayer){
        if (pPlayer.getResourceCards().get(StealableCard.Resource.WOOD) == 0 || pPlayer.getResourceCards().get(StealableCard.Resource.SHEEP) == 0){
            return false;
        }
        return true;
    }

    /*
    FUNCTIONS TO CHECK IF BUILDINGS CAN BE PLACED
     */

    public boolean checkSettlementPlaceEligibility(Intersection pIntersection, String pColor){
        if(pIntersection.getOccupancyFlag())
            return false;

        List<Intersection> iNeighbours = pIntersection.getIntersectionNeighbours();
        List<Edge> eNeighbours = pIntersection.getEdgeNeighbours();
        List<Hex> hNeighbours = pIntersection.getHexNeighbours();
        boolean water = true;
        for(Hex aHex:hNeighbours) {
            if(aHex.getTerrainType() != TerrainType.Sea)
                water =false;
        }
        if(water){  // settlement is surrounded by water
            return false;
        }

        for(Intersection aIntersection:iNeighbours) {
            if(aIntersection.getBuilding() != null)
                return  false;
        }

        for(Edge aEdge:eNeighbours){
            if(aEdge.getOccupancyFlag()){
                if(aEdge.getTransport().getOwner().getColor()==pColor)
                    return true;
            }
        }
        return false;
    }

    public boolean checkCityPlaceEligibility(Intersection pIntersection, String pColor){

          if(pIntersection.getBuilding() != null ){
              if(pIntersection.getBuilding().getOwner().getColor() == pColor && pIntersection.getBuilding().isCity()==false)
                  return true;
          }
        return false;
    }

    /*
    FUNCTIONS TO CHECK IF BUILDINGS CAN BE SETUP
     */

    public static boolean checkIntersectionSetupEligibility(Intersection pIntersection){
        if(pIntersection.getOccupancyFlag())
            return false;

        List<Intersection> iNeighbours = pIntersection.getIntersectionNeighbours();
        List<Edge> eNeighbours = pIntersection.getEdgeNeighbours();
        List<Hex> hNeighbours = pIntersection.getHexNeighbours();
        boolean water = true;
        for(Hex aHex:hNeighbours) {
            if(aHex.getTerrainType() != TerrainType.Sea)
                water =false;
        }
        if(water){  // settlement is surrounded by water
            return false;
        }
        for(Intersection aIntersection:iNeighbours) {
            if(aIntersection.getBuilding() != null)
                return  false;
        }
        return false;
    }

    /*
    FUNCTIONS TO CHECK IF TRANSPORTS CAN BE PLACED/SETUP
     */

    public static boolean checkRoadEligibility(Edge pEdge, String pColor){
        if(pEdge.getOccupancyFlag())
            return false;

        List<Edge> Neighbours = pEdge.getEdgeNeighbours();
        List<Hex> hNeighbours = pEdge.getHexNeighbours();
        List<Intersection> iNeighbours = pEdge.getIntersectionNeighbours();
        boolean water = true;
        for(Hex aHex:hNeighbours){
            if(aHex.getTerrainType() != TerrainType.Sea)
                water = false;
        }
        if(water) {
            return false;
        }
        for(Edge aEdge:Neighbours){
            if(aEdge.getOccupancyFlag()) {
                if (aEdge.getTransport().getUnit() == Unit.Transport.ROAD && aEdge.getTransport().getOwner().getColor() == pColor) {
                    return true;
                }
            }
        }
        for(Intersection aIntersection:iNeighbours){
            if(aIntersection.getOccupancyFlag()) {
                if (aIntersection.getBuilding()!=null && aIntersection.getBuilding().getOwner().getColor() == pColor) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkShipEligibility(Edge pEdge, String pColor){
        if(pEdge.getOccupancyFlag())
            return false;
        List<Edge> Neighbours = pEdge.getEdgeNeighbours();
        List<Hex> hNeighbours = pEdge.getHexNeighbours();
        List<Intersection> iNeighbours = pEdge.getIntersectionNeighbours();
        boolean water = false;
        for(Hex aHex:hNeighbours){
            if(aHex.getTerrainType() == TerrainType.Sea)
                water = true;
        }
        if(!water) {
            return false;
        }
        for(Edge aEdge:Neighbours){
            if(aEdge.getOccupancyFlag()) {
                if (aEdge.getTransport().getUnit() == Unit.Transport.SHIP && aEdge.getTransport().getOwner().getColor() == pColor) {
                    return true;
                }
            }
        }
        for(Intersection aIntersection:iNeighbours){
            if(aIntersection.getOccupancyFlag()) {
                if (aIntersection.getBuilding()!=null && aIntersection.getBuilding().getOwner().getColor() == pColor) {
                    return true;
                }
            }
        }
        return false;
    }

    public void playerTrade(Player pPlayer1, Player pPlayer2, HashMap<StealableCard.Resource, Integer> pResources1,
                            HashMap<StealableCard.Resource, Integer> pResources2,
                            HashMap<StealableCard.Commodity, Integer> pCommodities1,
                            HashMap<StealableCard.Commodity, Integer> pCommodities2){
        new PlayerTrade(pPlayer1, pPlayer2, pResources1, pResources2, pCommodities1, pCommodities2);
    }

    public void maritimeTrade(Player pPlayer, StealableCard.Resource pResource, int pAmount){
        new MaritimeTrade(pPlayer, pResource, pAmount);
    }

    public void endTurn(){
        aGame.setPhase(Game.GamePhase.TurnSecondPhase);
        nextPlayer();
    }

    private void nextPlayer(){
        aGame.updateTurn();
    }
}

