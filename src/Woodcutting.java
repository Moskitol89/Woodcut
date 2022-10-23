import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;


@ScriptManifest(name = "Woodcutting", description = "woodcut + low lvl firemaking", author = "Moskitol89",
        version = 0.1, category = Category.WOODCUTTING, image = "")
public class Woodcutting extends AbstractScript {

    private GameObject tree;
    private final Player PLAYER = Players.getLocal();
    private States state = States.WOODCUTTING;
    private int playerX;
    private int playerY;
    String treeName = "Tree";
    String logsName = "Logs";
    Area treeArea = new Area(3190, 3237, 3205, 3250);
    Condition animatingCondition = new Condition() {
        @Override
        public boolean verify() {
            return !PLAYER.isAnimating();
        }
    };

    public enum States {
        WOODCUTTING, FIREMAKING, BANK
    }
//    3095 3219

    @Override
    public int onLoop() {
        switch (state) {
            case WOODCUTTING -> {
                areaCheck(PLAYER.getTile(), treeArea);
                if (Inventory.isFull()) {
                    if (treeName.equals("Willow")) {
                        state = States.BANK;
                    } else {
                        state = States.FIREMAKING;
                    }
                    break;
                }
                if (Skill.WOODCUTTING.getLevel() > 15) {
                    treeName = "Oak";
                    treeArea = new Area(3093, 3213, 3132, 3223);
                }
                if (Skill.WOODCUTTING.getLevel() > 30) {
                    treeName = "Willow";
                    treeArea = new Area(3161, 3276, 3179, 3264);
                }
                MethodProvider.log("Woodcuting");
                if (PLAYER.isAnimating()) {
                    sleepUntil(animatingCondition, 60000L, 1000L);
                }
                tree = GameObjects.closest(treeName);
                if (!PLAYER.isMoving()) {
                    tree.interact("Chop down");
                }
            }
            case FIREMAKING -> {
                areaCheck(PLAYER.getTile(), treeArea);
                if (Skill.FIREMAKING.getLevel() > 15) {
                    logsName = "Oak logs";
                }
                if (Inventory.get(logsName) == null) {
                    state = States.WOODCUTTING;
                    break;
                }
                MethodProvider.log("Firemaking");
                if (PLAYER.isAnimating()) {
                    sleepUntil(animatingCondition, 60000L, 1000L);
                }
                if (!isFireUnderPlayer() && !PLAYER.isMoving()) {
                    Inventory.get("Tinderbox").useOn(logsName);
                } else {
                    moveToFreeTile(PLAYER.getTile());
                    log("FIRE FIRE FIRE");
                }
            }
            case BANK ->  {
                Bank.open();
                if(Bank.isOpen() && !PLAYER.isMoving()) {
                    Bank.depositAll("Willow logs");
                    sleep(500L, 1223L);
                    Bank.close();
                    state = States.WOODCUTTING;
                }
            }
        }
        return Calculations.random(900,2231);
    }

    private void areaCheck(Tile playerTile, Area area) {
        if (!area.contains(playerTile)) {
            log("Out from area!");
            Walking.walk(area.getRandomTile());
        }
    }


    private boolean isFireUnderPlayer() {
        log(GameObjects.closest("Fire").getTile());
        return GameObjects.closest("Fire").getTile().equals(PLAYER.getTile());
    }

    private void moveToFreeTile(Tile playerTile) {
        playerX = playerTile.getX();
        playerY = playerTile.getY();

        Tile nextTileRight = new Tile(playerX + 1, playerY);
        Tile nextTileLeft = new Tile(playerX - 1, playerY);
        Tile nextTileTop = new Tile(playerX, playerY + 1);
        Tile nextTileBottom = new Tile(playerX, playerY - 1);
        Tile nextTileRightTop = new Tile(playerX + 1, playerY + 1);
        Tile nextTileRightBottom = new Tile(playerX + 1, playerY - 1);
        Tile nextTileLeftTop = new Tile(playerX - 1, playerY + 1);
        Tile nextTileLeftBottom = new Tile(playerX - 1, playerY - 1);


        if ((Walking.canWalk(nextTileRight)) &&
                (GameObjects.getTopObjectOnTile(nextTileRight) == null)) {
            Walking.walk(nextTileRight);
        } else if ((Walking.canWalk(nextTileLeft)) &&
                (GameObjects.getTopObjectOnTile(nextTileLeft) == null)) {
            Walking.walk(nextTileLeft);
        } else if ((Walking.canWalk(nextTileTop)) &&
                (GameObjects.getTopObjectOnTile(nextTileTop) == null)) {
            Walking.walk(nextTileTop);
        } else if ((Walking.canWalk(nextTileBottom)) &&
                (GameObjects.getTopObjectOnTile(nextTileBottom) == null)) {
            Walking.walk(nextTileBottom);
        } else if ((Walking.canWalk(nextTileRightTop)) &&
                (GameObjects.getTopObjectOnTile(nextTileRightTop) == null)) {
            Walking.walk(nextTileRightTop);
        } else if ((Walking.canWalk(nextTileRightBottom)) &&
                (GameObjects.getTopObjectOnTile(nextTileRightBottom) == null)) {
            Walking.walk(nextTileRightBottom);
        } else if ((Walking.canWalk(nextTileLeftTop)) &&
                (GameObjects.getTopObjectOnTile(nextTileLeftTop) == null)) {
            Walking.walk(nextTileLeftTop);
        } else if ((Walking.canWalk(nextTileLeftBottom)) &&
                (GameObjects.getTopObjectOnTile(nextTileLeftBottom) == null)) {
            Walking.walk(nextTileLeftBottom);
        }
    }

}
