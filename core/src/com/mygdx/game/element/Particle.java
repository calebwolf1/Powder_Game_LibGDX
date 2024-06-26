package com.mygdx.game.element;

import com.mygdx.game.element.Neighborhood.Dir;
import com.mygdx.game.utils.Random;

public abstract class Particle extends Element {

    protected boolean ready;  // for applyGravity()

    protected abstract float getDensity();

    public Particle(int x, int y) {
        super(x, y);
    }

    // true if stayed in bounds, false if not and was removed
    public boolean applyGravity(Neighborhood neighbors) {
        if (!ready && Random.randBool(getDensity())) {
            ready = true;
        }
        if (ready && neighbors.isEmpty(Dir.DOWN)) {
            ready = false;
            return neighbors.move(Dir.DOWN);
        }
        return true;
    }

    // moves this Particle. Returns true if the Particle stayed in bounds, false if it went OOB
    // and was removed from the element map. Implementation note: consists of a series of "apply"
    // functions that move this Particle in different ways, with the same return rules as move().
    public abstract boolean move(Neighborhood neighbors);

}



// OLD PARTICLE METHODS BELOW

//    /**
//     * Move this Particle to one before the calculated new position and use the remaining vector’s
//     * magnitude as the chance it has to move into the final position. If there is an obstruction on
//     * the path, moves to one before the obstruction and ignores the remaining vector. Returns the
//     * new position of the particle.
//     * @param velocityMap the velocity map
//     * @param elementMap the element map
//     * @return whether the particle moved off the screen and was deleted or not.
//     */
//    public boolean move(ArrayMap<Vector2> velocityMap, ArrayMap<Element> elementMap) {
//        Vector2 newPos = getNewPos(velocityMap);
//        Position obstruction = getObstruction(newPos, elementMap);
//        if(obstruction != null) {
//            System.out.println("obstruction!");
//            return moveTo(obstruction.x, obstruction.y, elementMap);
//        }
//
//        // no obstruction
////        Vector2 velocity = newPos.cpy().sub(getPos());
////        Vector2 guaranteed = new Vector2(
////                x < newPos.x ? (float) Math.floor(newPos.x) : (float) Math.ceil(newPos.x),
////                y < newPos.y ? (float) Math.floor(newPos.y) : (float) Math.ceil(newPos.y));
//        int guaranteedX = x < newPos.x ? (int) Math.floor(newPos.x) : (int) Math.ceil(newPos.x);
//        int guaranteedY = y < newPos.y ? (int) Math.floor(newPos.y) : (int) Math.ceil(newPos.y);
//        Vector2 chance = new Vector2(newPos.x - guaranteedX, newPos.y - guaranteedY);
//        newPos.set(guaranteedX, guaranteedY);  // guaranteed new position
//        if(Math.abs(chance.x) > Math.abs(chance.y)) {
//            addChanceX(newPos, chance, elementMap);
//            addChanceY(newPos, chance, elementMap);
//        } else {
//            addChanceY(newPos, chance, elementMap);
//            addChanceX(newPos, chance, elementMap);
//        }
//
////        System.out.println(elementMap.get(newPos));  // should always be null
//        return moveTo((int) newPos.x, (int) newPos.y, elementMap);
//    }
//
//    private void addChanceX(Vector2 newPos, Vector2 chance, ArrayMap<Element> elementMap) {
//        if(!GameManager.boundsCheck((int) (newPos.x + Math.signum(chance.x)), (int) newPos.y)) {
//            if(Math.random() < Math.abs(chance.x)) {
//                newPos.x += Math.signum(chance.x);
//            }
//        } else {
//            if(elementMap.get((int) (newPos.x + Math.signum(chance.x)), (int) newPos.y) == null &&
//                    Math.random() < Math.abs(chance.x)) {
//                newPos.x += Math.signum(chance.x);
//            }
//        }
//    }
//
//    private void addChanceY(Vector2 newPos, Vector2 chance, ArrayMap<Element> elementMap) {
//        if(!GameManager.boundsCheck((int) newPos.x, (int) (newPos.y + Math.signum(chance.y)))) {
//            if(Math.random() < Math.abs(chance.y)) {
//                newPos.y += Math.signum(chance.y);
//            }
//        } else {
//            if(elementMap.get((int) newPos.x, (int) (newPos.y + Math.signum(chance.y))) == null &&
//                    Math.random() < Math.abs(chance.y)) {
//                newPos.y += Math.signum(chance.y);
//            }
//        }
//    }
//
//    // returns the spot 1 before the obstruction
//    private Position getObstruction(Vector2 end, ArrayMap<Element> elementMap) {
//        // grid traversal algorithm
//        // see: http://www.cse.yorku.ca/~amana/research/grid.pdf
//        // if this is too slow, consider Bresenham's algorithm
//        Position start = new Position(x, y);
////        int x = (int) Math.floor(start.x);
////        int y = (int) Math.floor(start.y);
//        int x = start.x;
//        int y = start.y;
//        float diffX = end.x - start.x;
//        float diffY = end.y - start.y;
//        int stepX = (int) Math.signum(diffX);
//        int stepY = (int) Math.signum(diffY);
//        float xOffset = end.x > start.x ? (float) Math.ceil(start.x) - start.x :
//                        start.x - (float) Math.floor(start.x);
//        float yOffset = end.y > start.y ? (float) Math.ceil(start.y) - start.y :
//                        start.y - (float) Math.floor(start.y);
//        float diffHyp = (float) Math.sqrt(diffX * diffX + diffY * diffY);
//        float tDeltaX = diffHyp / diffX;
//        float tDeltaY = -1 * diffHyp / diffY;
//        float tMaxX = tDeltaX * xOffset;
//        float tMaxY = tDeltaY * yOffset;
//
//        int taxiCabDist = (int) Math.abs(Math.floor(end.x) - Math.floor(start.x)) +
//                          (int) Math.abs(Math.floor(end.y) - Math.floor(start.y));
//        for(int t = 0; t < taxiCabDist; t++) {
//            int prevX = x;
//            int prevY = y;
//            if(Math.abs(tMaxX) < Math.abs(tMaxY)) {
//                tMaxX += tDeltaX;
//                x += stepX;
//                if(x < 0 || x >= elementMap.width) {
//                    System.out.println("here 1");
//                    return null;
//                }
//            } else {
//                tMaxY += tDeltaY;
//                y += stepY;
//                if(y < 0 || y >= elementMap.height) {
//                    System.out.println("here 2");
//                    return null;
//                }
//            }
//            if(elementMap.get(x, y) != null) {
//                System.out.println("obstruction!");
//                return new Position(prevX, prevY);
//            }
//        }
//        System.out.println("hi 2");
//        return null;
//    }
//
//    // moves this Particle to the new vector position. If the particle moved out of bounds, it
//    // removes it from the Element map
//    // return true if the element stayed within bounds, false if it did not and was removed.
//    // pre: the element map is empty at newPos
//    private boolean moveTo(int newX, int newY, ArrayMap<Element> elementMap) {
//        elementMap.set(x, y, null); // set old position to null in the Element map
//        x = newX;
//        y = newY;
//        if(GameManager.boundsCheck(x, y)) {
//            elementMap.set(newX, newY, this);
//            return true;
//        }
//        return false;
//    }


