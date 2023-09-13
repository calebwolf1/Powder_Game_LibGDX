package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;
import com.mygdx.game.GameManager;



public abstract class Particle extends Element {

    private Vector2 vel;  // the velocity of this Particle
    // possibly store int position

    /**
     * Calculates the farthest position this Particle can travel to if not obstructed. Based on
     * its current position, the velocity of the frame it is in, and what type of matter it is.
     * Defined in Solid, Liquid, and Gas, but can be overridden if a Particle has a different
     * movement pattern.
     * @return the distance this Particle should travel without obstructions.
     */
    public abstract Vector2 getNewPos(ArrayMap<Vector2> velMap);

    public Particle(Vector2 pos) {
        super(pos);
        vel = new Vector2();
    }

    public Vector2 getVel() {
        return vel;
    }

    /**
     * Interacts with neighboring Particles if it has any special behavior. Override with specific
     * behavior this is the case. Ex. switching layers with liquids of different densities,
     * exploding, salt and water turning into saltwater, etc.
     */
    public void interact() {

    }

    /**
     * Move this Particle to one before the calculated new position and use the remaining vector’s
     * magnitude as the chance it has to move into the final position. If there is an obstruction on
     * the path, moves to one before the obstruction and ignores the remaining vector. Returns the
     * new position of the particle.
     * @param oldPos
     * @param velocityMap
     * @param elementMap
     * @return
     */
    public boolean move(ArrayMap<Vector2> velocityMap, ArrayMap<Element> elementMap) {
        Vector2 newPos = getNewPos(velocityMap);
        Vector2 obstruction = getObstruction(newPos, elementMap);
        if(obstruction != null) {
            System.out.println("obstruction!");
            return moveTo(obstruction, elementMap);
        }

        // no obstruction
//        Vector2 velocity = newPos.cpy().sub(getPos());
        Vector2 guaranteed = new Vector2(
                getPos().x < newPos.x ? (float) Math.floor(newPos.x) : (float) Math.ceil(newPos.x),
                getPos().y < newPos.y ? (float) Math.floor(newPos.y) : (float) Math.ceil(newPos.y));
        Vector2 chance = newPos.cpy().sub(guaranteed);
        newPos.set(guaranteed);  // guaranteed new position
        if(Math.abs(chance.x) > Math.abs(chance.y)) {
            addChanceX(newPos, chance, elementMap);
            addChanceY(newPos, chance, elementMap);
        } else {
            addChanceY(newPos, chance, elementMap);
            addChanceX(newPos, chance, elementMap);
        }

//        System.out.println(elementMap.get(newPos));  // should always be null
        return moveTo(newPos, elementMap);
    }

    private void addChanceX(Vector2 newPos, Vector2 chance, ArrayMap<Element> elementMap) {
        if(!GameManager.boundsCheck((int) (newPos.x + Math.signum(chance.x)), (int) newPos.y)) {
            if(Math.random() < Math.abs(chance.x)) {
                newPos.x += Math.signum(chance.x);
            }
        } else {
            if(elementMap.get((int) (newPos.x + Math.signum(chance.x)), (int) newPos.y) == null &&
                    Math.random() < Math.abs(chance.x)) {
                newPos.x += Math.signum(chance.x);
            }
        }
    }

    private void addChanceY(Vector2 newPos, Vector2 chance, ArrayMap<Element> elementMap) {
        if(!GameManager.boundsCheck((int) newPos.x, (int) (newPos.y + Math.signum(chance.y)))) {
            if(Math.random() < Math.abs(chance.y)) {
                newPos.y += Math.signum(chance.y);
            }
        } else {
            if(elementMap.get((int) newPos.x, (int) (newPos.y + Math.signum(chance.y))) == null &&
                    Math.random() < Math.abs(chance.y)) {
                newPos.y += Math.signum(chance.y);
            }
        }
    }

    // returns the spot 1 before the obstruction
    private Vector2 getObstruction(Vector2 end, ArrayMap<Element> elementMap) {
        // grid traversal algorithm
        // see: http://www.cse.yorku.ca/~amana/research/grid.pdf
        // if this is too slow, consider Bresenham's algorithm
        Vector2 start = getPos();
        int x = (int) Math.floor(start.x);
        int y = (int) Math.floor(start.y);
        float diffX = end.x - start.x;
        float diffY = end.y - start.y;
        int stepX = (int) Math.signum(diffX);
        int stepY = (int) Math.signum(diffY);
        float xOffset = end.x > start.x ? (float) Math.ceil(start.x) - start.x :
                        start.x - (float) Math.floor(start.x);
        float yOffset = end.y > start.y ? (float) Math.ceil(start.y) - start.y :
                        start.y - (float) Math.floor(start.y);
        float diffHyp = (float) Math.sqrt(diffX * diffX + diffY * diffY);
        float tDeltaX = diffHyp / diffX;
        float tDeltaY = -1 * diffHyp / diffY;
        float tMaxX = tDeltaX * xOffset;
        float tMaxY = tDeltaY * yOffset;

        int taxiCabDist = (int) Math.abs(Math.floor(end.x) - Math.floor(start.x)) +
                          (int) Math.abs(Math.floor(end.y) - Math.floor(start.y));
        for(int t = 0; t < taxiCabDist; t++) {
            int prevX = x;
            int prevY = y;
            if(Math.abs(tMaxX) < Math.abs(tMaxY)) {
                tMaxX += tDeltaX;
                x += stepX;
                if(x < 0 || x >= elementMap.width) {
                    System.out.println("here 1");
                    return null;
                }
            } else {
                tMaxY += tDeltaY;
                y += stepY;
                if(y < 0 || y >= elementMap.height) {
                    System.out.println("here 2");
                    return null;
                }
            }
            if(elementMap.get(x, y) != null) {
                System.out.println("obstruction!");
                return new Vector2(prevX, prevY);
            }
        }

        return null;
    }

    // moves this Particle to the new vector position. If the particle moved out of bounds, it
    // removes it from the Element map
    // return true if the element stayed within bounds, false if it did not and was removed.
    // pre: the element map is empty at newPos
    private boolean moveTo(Vector2 newPos, ArrayMap<Element> elementMap) {
//        System.out.println(elementMap.get(newPos));
        elementMap.set(getPos(), null); // set old position to null in the Element map
        setPos(newPos);
        if(GameManager.boundsCheck(getPos())) {
            elementMap.set(newPos, this);
            return true;
        }
        return false;
    }

}
