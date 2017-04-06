package com.example.models.gameModels;
import com.example.repositories.GameRepository;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class GameManager {
    private static GameManager gameManager = new GameManager();
    private static Game aGame;

    @Autowired
    private static GameRepository gameRepository;


    private GameManager() {
    }

    public static GameManager instance() {
        return gameManager;
    }

    public static ArrayList<Player> createPlayers(ArrayList<String> pPlayerNames) {
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

    public static void createGame(int pVPsToWin, ArrayList<String> playerNames) {
        List<Player> aPlayers = createPlayers(playerNames);
        Board aBoard = new Board();
        Game game = new Game( pVPsToWin, aPlayers, aBoard);
        aGame = game;
    }

    public static void saveGame(){
        gameRepository.save(aGame);
    }

    public static Game getGame(){
        return aGame;
    }


    //add and remove resources
    public void addResource(StealableCard.Resource pResource, int pAmount) {
        aGame.getResourceCards().put(pResource, aGame.getResourceCards().get(pResource) + pAmount);
    }
    public void removeResource(StealableCard.Resource pResource, int pAmount) {
        assert (aGame.getResourceCards().get(pResource) >= pAmount);
        aGame.getResourceCards().put(pResource, aGame.getResourceCards().get(pResource) - pAmount);
    }

    //add and remove commodities
    public void addCommodity(StealableCard.Commodity pCommodity, int pAmount) {
        aGame.getCommodityCards().put(pCommodity, aGame.getCommodityCards().get(pCommodity) + pAmount);
    }
    public void removeCommodity(StealableCard.Commodity pCommodity, int pAmount) {
        assert (aGame.getCommodityCards().get(pCommodity) >= pAmount);
        aGame.getCommodityCards().put(pCommodity, aGame.getCommodityCards().get(pCommodity) - pAmount);
    }

    private void waitToSet(){
        try {
            Thread.sleep(10000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
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
            if (removeGold()) {
                pOwner.addGold();
            }
        }
        else {
            StealableCard.Resource resource = pTerrainType.giveResource();
            assert(aGame.getResourceCards().get(resource) > 0);
            pOwner.addResource(resource, 1);
            removeResource(resource, 1);
        }

        if (isCity) {
            StealableCard.Resource resource = pTerrainType.giveResource();
            if (pTerrainType == TerrainType.Hills || pTerrainType == TerrainType.Fields){
                assert(aGame.getResourceCards().get(resource) > 0);
                pOwner.addResource(resource, 1);
                removeResource(resource, 1);
            }
            else if (pTerrainType == TerrainType.GoldMine){
                if (removeGold()){
                    pOwner.addGold();
                }
            }
            else{
                StealableCard.Commodity commodity = pTerrainType.giveCommodity();
                assert(aGame.getCommodityCards().get(commodity) > 0);
                pOwner.addCommodity(commodity, 1);
                removeCommodity(commodity, 1);
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
                    removeResource(resource, 1);
                }
            }
        }
    }

    private boolean removeGold(){
        if (aGame.getGoldBank() >= 2) {
            aGame.setGoldBank(aGame.getGoldBank() - 2);
            return true;
        }
        return false;
    }

    private void paySettlement(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.SHEEP, 1);
        addResource(StealableCard.Resource.SHEEP, 1);
        pPlayer.removeResource(StealableCard.Resource.WHEAT, 1);
        addResource(StealableCard.Resource.WHEAT, 1);
        pPlayer.removeResource(StealableCard.Resource.BRICK, 1);
        addResource(StealableCard.Resource.BRICK, 1);
        pPlayer.removeResource(StealableCard.Resource.WOOD, 1);
        addResource(StealableCard.Resource.WOOD, 1);
    }

    private void payCity(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.WHEAT, 2);
        addResource(StealableCard.Resource.WHEAT, 2);
        pPlayer.removeResource(StealableCard.Resource.ORE, 3);
        addResource(StealableCard.Resource.ORE, 3);
    }

    private void payRoad(Player pPlayer){
        pPlayer.removeResource(StealableCard.Resource.BRICK, 1);
        addResource(StealableCard.Resource.BRICK, 1);
        pPlayer.removeResource(StealableCard.Resource.WOOD, 1);
        addResource(StealableCard.Resource.WOOD, 1);
    }

    public void placeSettlement(Player pPlayer, Intersection pIntersection){
        assert(!pIntersection.getOccupancyFlag());
        assert(checkIntersectionEligibility(pIntersection, pPlayer));
        assert(checkBuySettlement(pPlayer));

        paySettlement(pPlayer);
        OwnedBuilding settlement = pPlayer.removeBuilding(Unit.Building.SETTLEMENT);
        pIntersection.setBuilding(settlement);
    }

    public void placeCity(Player pPlayer, Intersection pIntersection){
        assert(pIntersection.getOccupancyFlag());
        assert(pIntersection.getBuilding() != null && !pIntersection.getBuilding().isCity() && pIntersection.getBuilding().getOwner() == pPlayer);
        assert(checkBuyCity(pPlayer));

        payCity(pPlayer);
        OwnedBuilding city = pPlayer.removeBuilding(Unit.Building.CITY);
        pPlayer.addBuilding(Unit.Building.SETTLEMENT);
        pIntersection.setBuilding(city);
    }

    public void placeRoad(Player pPlayer, Edge pEdge){
        assert(!pEdge.getOccupancyFlag());
        assert(checkEdgeEligibility(pEdge, pPlayer));
        assert(checkBuyRoad(pPlayer));

        payRoad(pPlayer);
        OwnedTransport road = pPlayer.removeTransport(Unit.Transport.ROAD);
        pEdge.setTransport(road);
    }

    private boolean checkIntersectionEligibility(Intersection pIntersection, Player pPlayer){
        boolean eligible1 = true;
        List<Intersection> neighbourIntersections = pIntersection.getIntersectionNeighbours();
        for (Intersection intersection: neighbourIntersections){    //Iterate through all neighbours to see if they are occupied
            if (intersection.getOccupancyFlag()){
                eligible1 = false;
                break;
            }
        }

        List<Edge> neighbourEdges = pIntersection.getEdgeNeighbours();
        boolean eligible2 = false;
        for (Edge edge: neighbourEdges){
            if (edge.getOccupancyFlag()){
                if (edge.getTransport().getOwner() == pPlayer) {
                    eligible2 = true;
                    break;
                }
            }
        }
        return (eligible1 && eligible2);
    }

    private boolean checkEdgeEligibility(Edge pEdge, Player pPlayer){
        List<Edge> neighbourEdges = pEdge.getEdgeNeighbours();
        boolean eligible = false;
        for (Edge edge: neighbourEdges) {
            if (edge.getOccupancyFlag()) {
                if (edge.getTransport().getOwner() == pPlayer) {
                    eligible = true;
                    break;
                }
            }
        }
        return eligible;
    }

    private boolean checkBuySettlement(Player pPlayer) {
        if (pPlayer.getResourceCards().get(StealableCard.Resource.SHEEP) == 0 ||
                pPlayer.getResourceCards().get(StealableCard.Resource.ORE) == 0 ||
                pPlayer.getResourceCards().get(StealableCard.Resource.WOOD) == 0 ||
                pPlayer.getResourceCards().get(StealableCard.Resource.BRICK) == 0){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean checkBuyCity(Player pPlayer){
        if (pPlayer.getResourceCards().get(StealableCard.Resource.ORE) < 3 || pPlayer.getResourceCards().get(StealableCard.Resource.WHEAT) < 2){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean checkBuyRoad(Player pPlayer){
        if (pPlayer.getResourceCards().get(StealableCard.Resource.WOOD) == 0 || pPlayer.getResourceCards().get(StealableCard.Resource.BRICK) == 0){
            return false;
        }
        else{
            return true;
        }

    }

    public void setupSettlement(Player pPlayer, Intersection pIntersection){
        assert (!pIntersection.getOccupancyFlag());
       // assert (checkIntersectionSetupEligibility(pIntersection));

        OwnedBuilding settlement = pPlayer.removeBuilding(Unit.Building.SETTLEMENT);
        pIntersection.setBuilding(settlement);
    }

    public void setupCity(Player pPlayer, Intersection pIntersection){
        assert(!pIntersection.getOccupancyFlag());
        //assert(checkIntersectionSetupEligibility(pIntersection));

        OwnedBuilding city = pPlayer.removeBuilding(Unit.Building.CITY);
        pIntersection.setBuilding(city);
    }

    public void setupRoad(Player pPlayer, Edge pEdge){
        assert(!pEdge.getOccupancyFlag());
       // assert(checkEdgeSetupEligibility(pEdge, pPlayer));

        OwnedTransport road = pPlayer.removeTransport(Unit.Transport.ROAD);
        pEdge.setTransport(road);
    }

    public static boolean checkSettlementSetupEligibility(Intersection pIntersection, String pColor){
        if(pIntersection.getOccupancyFlag())
            return false;

        List<Intersection> iNeighbours = pIntersection.getIntersectionNeighbours();
        List<Edge> eNeighbours = pIntersection.getEdgeNeighbours();

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

    public static boolean checkCitySetupEligibility(Intersection pIntersection, String pColor){

          if(pIntersection.getBuilding() != null ){
              if(pIntersection.getBuilding().getOwner().getColor()==pColor && pIntersection.getBuilding().isCity()==false)
                  return true;
          }
        return false;
    }

    public static boolean checkRoadSetupEligibility(Edge pEdge, String pColor){
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

    public static boolean checkShipSetupEligibility(Edge pEdge, String pColor){
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

