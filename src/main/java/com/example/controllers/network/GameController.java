package com.example.controllers.network;

import com.example.models.gameModels.*;
import com.example.viewobjects.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.json.*;
import com.google.gson.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Controller
public class GameController {

    private static ArrayList<String> currPlayerList = new ArrayList<String>(); // Get a list of players too?
    private static Game aGame;
    private static Map<String, Player> aPlayers ; // here

    private static int placeSettlementAndRoadCounter = 0;
    private static int placeCityAndRoadCounter = 0;
    private static int currNumPlayers;
    private static int currPlayerTurn = 0;
    private static final PlayerAndPhase pap = new PlayerAndPhase();
    private static int turnCounter = 0;
    public static void setCurrPlayerList(ArrayList<String> pList){
        for (String s : pList){
            currPlayerList.add(s);
        }

        currNumPlayers = currPlayerList.size();
        pap.setSetup1(true);
        pap.setSetup2(false);
    }

    @RequestMapping(value="/game", method= RequestMethod.GET)
    public String game(ModelMap model, Principal caller) {

        String name = caller.getName(); //get logged in username
        model.addAttribute("username", name);
        model.addAttribute("startingPlayer",currPlayerList.get(0));

        for(int i = 0 ; i < currPlayerList.size(); i++){
            if (currPlayerList.get(i).matches(name)){
                String color = GameManager.instance().getGame().getPlayers().get(i).getColor();
                model.addAttribute("myColor", color);
            }
        }

        for(int i = 0 ; i < currPlayerList.size(); i++){
            model.addAttribute("player"+(i+1),currPlayerList.get(i));
        }

        for(int i = 0 ; i < currPlayerList.size(); i++){
            model.addAttribute("player"+(i+1)+"_c", GameManager.getGame().getPlayers().get(i).getColor());
        }
        aGame = GameManager.getGame();

        return "game";
    }


    @MessageMapping("/placesettlement")
    @SendTo("/topic/settlement")
    public ViewPiece placeSettlement(ViewPiece pNew, Principal caller){
        // TODO Check if player has enough resources

        Intersection Checker = GameManager.getGame().getBoard().getIntersections().get(pNew.getId());
        boolean valid = GameManager.checkSettlementSetupEligibility(Checker,pNew.getColor());

        if(valid)
        {
            // TODO Spend players resources
            // TODO Add settlement to Intersection
        }
        pNew.setValid(valid);
        return pNew;
    }

    @MessageMapping("/placecity")
    @SendTo("/topic/city")
    public ViewPiece placeCity(ViewPiece pNew, Principal caller){
            // TODO Check if player has enough resources

        Intersection Checker = GameManager.getGame().getBoard().getIntersections().get(pNew.getId());
        boolean valid = GameManager.checkCitySetupEligibility(Checker,pNew.getColor());

        if(valid)
        {
            // TODO Spend players resources
            // TODO Add city to Intersection
        }
        pNew.setValid(valid);
        return pNew;
    }

    @MessageMapping("/placeroad")
    @SendTo("/topic/road")
    public ViewPiece placeRoad(ViewPiece pNew, Principal caller){

        // TODO Check if player has enough resources

        System.out.println(pNew.getId());
        Edge Checker = GameManager.getGame().getBoard().getEdges().get(pNew.getId());
        boolean valid = GameManager.checkRoadSetupEligibility(Checker,pNew.getColor());

        if(valid){
            // TODO Spend players resources
            // TODO Add road to edge
        }
        pNew.setValid(valid);
        return pNew;
    }

    @MessageMapping("/placeship")
    @SendTo("/topic/ship")
    public ViewPiece placeShip(ViewPiece pNew, Principal caller){

        // TODO Check if player has enough resources

        System.out.println(pNew.getId());
        Edge Checker = GameManager.getGame().getBoard().getEdges().get(pNew.getId());
        boolean valid= GameManager.checkShipSetupEligibility(Checker,pNew.getColor());

        if(valid){
            // TODO Spend players resources
            // TODO Add ship to edge
        }
        pNew.setValid(valid);
        return pNew;
    }

    // SETUP IS FIRST 2 TURNS
    @MessageMapping("/setupsettlement")
    @SendTo("/topic/settlement")
    public ViewPiece setupSettlement(ViewPiece pNew, Principal caller){

        Intersection Checker = GameManager.getGame().getBoard().getIntersections().get(pNew.getId());
        boolean valid = GameManager.freeIntersectionEligibility(Checker);

        if(valid)
        {
            // TODO Add settlement to Intersection
        }
        pNew.setValid(valid);
        return pNew;
    }

    @MessageMapping("/setupcity")
    @SendTo("/topic/city")
    public ViewPiece setupCity(ViewPiece pNew, Principal caller){

        Intersection Checker = GameManager.getGame().getBoard().getIntersections().get(pNew.getId());
        boolean valid = GameManager.freeIntersectionEligibility(Checker);

        if(valid)
        {
            // TODO Add city to Intersection
        }
        pNew.setValid(valid);
        return pNew;
    }

    @MessageMapping("/setuproad")
    @SendTo("/topic/road")
    public ViewPiece setupRoad(ViewPiece pNew, Principal caller){

        System.out.println(pNew.getId());
        Edge Checker = GameManager.getGame().getBoard().getEdges().get(pNew.getId());
        boolean valid = GameManager.checkRoadSetupEligibility(Checker,pNew.getColor());

        if(valid){
            // TODO Add road to edge
        }
        pNew.setValid(valid);
        return pNew;
    }

    @SendTo("/topic/playerIncrement")
    public PlayerIncrement setupPayout(){

        PlayerIncrement increment = new PlayerIncrement();
       // aGame.setupPayout();
        setPlayerIncrement(increment);
        return increment;
    }

    @MessageMapping("/rolldice")
    @SendTo("/topic/dice")
    public DiceRoll showDice(DiceRoll pDice){
        //aGame.rollDice(pDice.getYellow(), pDice.getRed(), pDice.getEvent());*/
        /*diceRollPayout();*/
        return pDice;
    }

    @SendTo("/topic/playerIncrement")
    private PlayerIncrement diceRollPayout(){

        PlayerIncrement increment = new PlayerIncrement();
        //setPlayerIncrement(increment);
        return increment;
    }

    private void setPlayerIncrement(PlayerIncrement pIncrement){
        /*
        for (String pUsername : currPlayerList){
            for (Player player : aGame.getPlayers()) {
                if (pUsername.equals(player.getUsername())) {
                    int index = currPlayerList.indexOf(player.getUsername());
                    switch (index) {
                        case 1:
                            pIncrement.setp1(
                                    player.getGold(),
                                    player.getResourceCards().get(StealableCard.Resource.ORE),
                                    player.getResourceCards().get(StealableCard.Resource.BRICK),
                                    player.getResourceCards().get(StealableCard.Resource.WOOD),
                                    player.getResourceCards().get(StealableCard.Resource.SHEEP),
                                    player.getCommodityCards().get(StealableCard.Commodity.COIN),
                                    player.getCommodityCards().get(StealableCard.Commodity.CLOTH),
                                    player.getCommodityCards().get(StealableCard.Commodity.PAPER));
                        case 2:
                            pIncrement.setp2(
                                    player.getGold(),
                                    player.getResourceCards().get(StealableCard.Resource.ORE),
                                    player.getResourceCards().get(StealableCard.Resource.BRICK),
                                    player.getResourceCards().get(StealableCard.Resource.WOOD),
                                    player.getResourceCards().get(StealableCard.Resource.SHEEP),
                                    player.getCommodityCards().get(StealableCard.Commodity.COIN),
                                    player.getCommodityCards().get(StealableCard.Commodity.CLOTH),
                                    player.getCommodityCards().get(StealableCard.Commodity.PAPER));
                        case 3:
                            pIncrement.setp3(
                                    player.getGold(),
                                    player.getResourceCards().get(StealableCard.Resource.ORE),
                                    player.getResourceCards().get(StealableCard.Resource.BRICK),
                                    player.getResourceCards().get(StealableCard.Resource.WOOD),
                                    player.getResourceCards().get(StealableCard.Resource.SHEEP),
                                    player.getCommodityCards().get(StealableCard.Commodity.COIN),
                                    player.getCommodityCards().get(StealableCard.Commodity.CLOTH),
                                    player.getCommodityCards().get(StealableCard.Commodity.PAPER));
                        case 4:
                            pIncrement.setp4(
                                    player.getGold(),
                                    player.getResourceCards().get(StealableCard.Resource.ORE),
                                    player.getResourceCards().get(StealableCard.Resource.BRICK),
                                    player.getResourceCards().get(StealableCard.Resource.WOOD),
                                    player.getResourceCards().get(StealableCard.Resource.SHEEP),
                                    player.getCommodityCards().get(StealableCard.Commodity.COIN),
                                    player.getCommodityCards().get(StealableCard.Commodity.CLOTH),
                                    player.getCommodityCards().get(StealableCard.Commodity.PAPER));
                    }
                }
            }
        }
        */
    }

    @MessageMapping("/endturn")
    @SendTo("/topic/turninfo")
    public PlayerAndPhase endTurn(Principal user){



        if(turnCounter == (currPlayerList.size()-1)){
            System.out.println("first if");
            System.out.println(currPlayerList.size()-1);
            Collections.reverse(currPlayerList);
            currPlayerTurn = 0;
            pap.setSetup1(false);
            pap.setSetup2(true);

        }else if(turnCounter == (2*(currPlayerList.size())-1)){
            System.out.println("second if");
            System.out.println(currPlayerList.size()-1);
            Collections.reverse(currPlayerList);
            currPlayerTurn = 0;
            pap.setSetup1(false);
            pap.setSetup2(false);

        }else{
            currPlayerTurn = (currPlayerTurn + 1)%currNumPlayers;
        }

        pap.setUsername(currPlayerList.get(currPlayerTurn));

        System.out.println("Player's Turn "+ currPlayerList.get(currPlayerTurn));
        System.out.println("turn count = "+currPlayerTurn);

        this.turnCounter++;

        return pap;
    }


    @MessageMapping("/edge")
    public void getEdge(ViewEdge pEdge) throws Exception{

        Edge aEdge = new Edge(pEdge.getId());
    //    System.out.println(aEdge.getId());
    //    System.out.println(aEdge.getX());
    //    System.out.println(aEdge.getY());
    //    System.out.println(aEdge.getPrefix());
        GameManager.getGame().getBoard().getEdges().put(aEdge.getId(),aEdge);

    }

    @MessageMapping("/hex")
    public void getHex(String bigJson) throws Exception{

        JSONArray aArray = new JSONArray(bigJson);
        Gson gson = new Gson();

        for(int i=0;i<aArray.length();i++) {
            JSONObject jsonHex = aArray.getJSONObject(i);

            ViewHex pHex = gson.fromJson(jsonHex.toString(), ViewHex.class);

            switch (pHex.getTerrainType()) {
                case "wood":
                    LandHex aHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.Forest);
                    GameManager.getGame().getBoard().getHexes().put(aHex.getId(), aHex);
                    GameManager.getGame().getBoard().getLandHexes().get(aHex.getProductionNumber()).add(aHex);
                    break;
                case "ore":
                    LandHex bHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.Mountains);
                    GameManager.getGame().getBoard().getHexes().put(bHex.getId(), bHex);
                    GameManager.getGame().getBoard().getLandHexes().get(bHex.getProductionNumber()).add(bHex);
                    break;
                case "brick":
                    LandHex cHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.Hills);
                    GameManager.getGame().getBoard().getHexes().put(cHex.getId(), cHex);
                    GameManager.getGame().getBoard().getLandHexes().get(cHex.getProductionNumber()).add(cHex);
                    break;
                case "sheep":
                    LandHex dHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.Pasture);
                    GameManager.getGame().getBoard().getHexes().put(dHex.getId(), dHex);
                    GameManager.getGame().getBoard().getLandHexes().get(dHex.getProductionNumber()).add(dHex);
                    break;
                case "gold":
                    LandHex eHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.GoldMine);
                    GameManager.getGame().getBoard().getHexes().put(eHex.getId(), eHex);
                    GameManager.getGame().getBoard().getLandHexes().get(eHex.getProductionNumber()).add(eHex);
                    break;
                case "wheat":
                    LandHex fHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.Fields);
                    GameManager.getGame().getBoard().getHexes().put(fHex.getId(), fHex);
                    GameManager.getGame().getBoard().getLandHexes().get(fHex.getProductionNumber()).add(fHex);
                    break;
                case "desert":
                    LandHex gHex = new LandHex(pHex.getId(), pHex.getNumber(), TerrainType.Desert);
                    GameManager.getGame().getBoard().getHexes().put(gHex.getId(), gHex);
                    GameManager.getGame().getBoard().getLandHexes().get(gHex.getProductionNumber()).add(gHex);
                    break;
                case "sea":
                    SeaHex hHex = new SeaHex(pHex.getId());
                default:
                     hHex = new SeaHex(pHex.getId());
            }

        }

    }

    @MessageMapping("/intersection")
    public void getIntersection(ViewIntersection pIntersection) throws Exception{

       // System.out.println("Intersection");
        Intersection aIntersection = new Intersection(pIntersection.getId(), HarbourType.None);
        GameManager.getGame().getBoard().getIntersections().put(aIntersection.getId(),aIntersection);
    }

    @MessageMapping("/setNeighbours")
    public void setNeighbours() throws Exception
    {
        GameManager.getGame().getBoard().setAllNeighbours();
    }
}