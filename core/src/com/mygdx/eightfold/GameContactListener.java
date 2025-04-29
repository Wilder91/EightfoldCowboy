package com.mygdx.eightfold;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import helper.ContactType;
import objects.GameEntity;
import objects.animals.farm_animals.Chicken;
import objects.enemies.ThicketSaint;
import objects.enemies.ThicketSaintManager;
import objects.inanimate.inanimate_helpers.DoorManager;
import objects.humans.NPC;
import objects.humans.NPCManager;
import objects.inanimate.Door;

/**
 * Handles all collision events in the game world
 */
public class GameContactListener implements ContactListener {

    private ScreenInterface screenInterface;
    private int contactCounter = 0;

    public GameContactListener(ScreenInterface screenInterface) {
        this.screenInterface = screenInterface;
    }

    @Override
    public void beginContact(Contact contact) {



        //System.out.println("begin contact");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getUserData() instanceof BodyUserData &&
                ((BodyUserData)fixtureA.getUserData()).getType() == ContactType.ENEMYATTACK) {
            System.out.println("ATTACK sensor detected in contact!");
        }
        if (fixtureB.getUserData() instanceof BodyUserData &&
                ((BodyUserData)fixtureB.getUserData()).getType() == ContactType.ENEMYATTACK) {
            System.out.println("ATTACK sensor detected in contact!");
        }

        if (fixtureA.getUserData() instanceof BodyUserData &&
                ((BodyUserData)fixtureA.getUserData()).getType() == ContactType.ATTACK) {
            System.out.println("ATTACK sensor detected in contact!");
        }
        if (fixtureB.getUserData() instanceof BodyUserData &&
                ((BodyUserData)fixtureB.getUserData()).getType() == ContactType.ATTACK) {
            System.out.println("ATTACK sensor detected in contact!");
        }
        boolean isSensorContact = fixtureA.isSensor() || fixtureB.isSensor();

        if (isSensorContact) {
            // System.out.println(fixtureA.getUserData() + " " + fixtureB.getUserData());
            // Get the user data from the fixtures to identify them

            Object userDataA = fixtureA.getUserData();
            Object userDataB = fixtureB.getUserData();

            // Handle different types of sensor contacts
            if (fixtureA.isSensor() && userDataA instanceof BodyUserData) {
                BodyUserData sensorData = (BodyUserData) userDataA;
                handleSensorContact(sensorData, fixtureB, true);
            }

            if (fixtureB.isSensor() && userDataB instanceof BodyUserData) {
                BodyUserData sensorData = (BodyUserData) userDataB;
                handleSensorContact(sensorData, fixtureA, true);
            }
        }

        // Early exit if fixtures don't have proper user data
        if (!(fixtureA.getUserData() instanceof BodyUserData) ||
                !(fixtureB.getUserData() instanceof BodyUserData)) {
            // Special case for ThicketSaint sensor
            //System.out.println("here?");
            handleSpecialSensorCases(fixtureA, fixtureB);
            return;
        } else {

            BodyUserData userDataA = (BodyUserData) fixtureA.getUserData();
            BodyUserData userDataB = (BodyUserData) fixtureB.getUserData();

            //System.out.println("Contact between: " + userDataA.getType() + " and " + userDataB.getType());

            // Handle each contact type with independent checks
            handleAttackContacts(userDataA, userDataB);
            handlePlayerDoorContacts(userDataA, userDataB);
            handlePlayerNPCContacts(userDataA, userDataB);
            handleEnemyAttackContacts(userDataA, userDataB);
            handleBirdContacts(userDataA, userDataB, fixtureA.getBody(), fixtureB.getBody());
            handleChickenContacts(userDataA, userDataB, fixtureA.getBody(), fixtureB.getBody());
            handleEnemyPlayerContacts(userDataA, userDataB, contact);
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Handle only fixtures with proper user data
        if (fixtureA.getUserData() instanceof BodyUserData &&
                fixtureB.getUserData() instanceof BodyUserData) {

            BodyUserData userDataA = (BodyUserData) fixtureA.getUserData();
            BodyUserData userDataB = (BodyUserData) fixtureB.getUserData();

            // Handle player leaving door area
            handlePlayerLeavingDoor(userDataA, userDataB);
            // Special case for ThicketSaint sensor
            handleEndOfSpecialSensorCases(fixtureA, fixtureB);

            // Add other end contact handlers here
        }



        else if (fixtureB.getUserData() == "playerSensor" &&
                fixtureA.getUserData() instanceof BodyUserData) {

            // Handle player sensor end contact

        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Implement as needed
    }

    /**
     * Handles all attack-related contacts
     */


    /**
     * Handles player-door interactions
     */
    private void handlePlayerDoorContacts(BodyUserData userDataA, BodyUserData userDataB) {
        boolean isPlayerDoorContact =
                (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) ||
                        (userDataB.getType() == ContactType.PLAYER && userDataA.getType() == ContactType.DOOR);

        if (isPlayerDoorContact) {
            BodyUserData doorData = (userDataA.getType() == ContactType.DOOR) ? userDataA : userDataB;
            Door door = DoorManager.getDoorById(doorData.getId());
            if (door != null) {
              //  System.out.println("Player began contact with door: " + door.getName());
                door.playerContact();
            }
        }
    }

    /**
     * Handles player-NPC interactions
     */
    private void handlePlayerNPCContacts(BodyUserData userDataA, BodyUserData userDataB) {
        boolean isPlayerNPCContact =
                (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.NPC) ||
                        (userDataB.getType() == ContactType.PLAYER && userDataA.getType() == ContactType.NPC);

        if (isPlayerNPCContact) {
            BodyUserData npcData = (userDataA.getType() == ContactType.NPC) ? userDataA : userDataB;
            NPC npc = NPCManager.getNPCById(npcData.getId());
            if (npc != null) {
                npc.playerContact(npc);
            }
        }
    }

    private void handleAttackContacts(BodyUserData userDataA, BodyUserData userDataB) {
        // Check for player attack hitting enemy

        boolean isAttackHittingEnemy =
                (userDataA.getType() == ContactType.ATTACK && userDataB.getType() == ContactType.ENEMY) ||
                        (userDataB.getType() == ContactType.ATTACK && userDataA.getType() == ContactType.ENEMY);
        if (isAttackHittingEnemy) {
            contactCounter++;
            //System.out.println("ATTACK HIT ENEMY! Count: " + contactCounter);

            // Get the enemy user data
            BodyUserData enemyData = (userDataA.getType() == ContactType.ENEMY) ? userDataA : userDataB;
            BodyUserData attackData = (userDataA.getType() == ContactType.ATTACK) ? userDataA : userDataB;
           // System.out.println("Enemy hit: " + enemyData + " by attack: " + attackData);

            // Get the enemy ID
            int enemyId = enemyData.getId();

            // Get the ThicketSaint from the manager
            ThicketSaint thicketSaint = ThicketSaintManager.getEnemyById(enemyId);

            if (thicketSaint != null) {
                //System.out.println("YOOO");
                thicketSaint.takeDamage();

                // Play hit sound
                Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
                //sound.play(0.05f);
            } else {
                System.out.println("Could not find enemy with ID: " + enemyId);
            }
        }
    }

    /**
     * Handles enemy attack hitting player
     */
    private void handleEnemyAttackContacts(BodyUserData userDataA, BodyUserData userDataB) {
        boolean isEnemyAttackHittingPlayer =
                (userDataA.getType() == ContactType.ENEMYATTACK && userDataB.getType() == ContactType.PLAYER) ||
                        (userDataB.getType() == ContactType.ENEMYATTACK && userDataA.getType() == ContactType.PLAYER);

        if (isEnemyAttackHittingPlayer) {
            Player player = screenInterface.getPlayer();
            player.takeDamage();
            //System.out.println("PLAYER HIT BY ENEMY ATTACK!");
            // Add player damage logic here
            // screenInterface.damagePlayer(10);
        }
    }

    private void handleEnemyPlayerContacts(BodyUserData userDataA, BodyUserData userDataB, Contact contact) {
        //System.out.println("PLAYER HIT BY ENEMY");
        contact.setEnabled(true);  // Keep collision detection enabled
        contact.setRestitution(-10); // No bounce
        contact.setFriction(0);    // No friction

        // This is the key part - this makes the collision "sensor-like" but still registers as a collision
        contact.setTangentSpeed(0);

        // Reset velocity to zero




        // Get the enemy body
        BodyUserData enemyData = (userDataA.getType() == ContactType.ENEMY) ? userDataA : userDataB;
        Body enemyBody = enemyData.getBody();
        enemyBody.setLinearVelocity(0, 0);

    }

    /**
     * Handles bird-related contacts
     */
    private void handleBirdContacts(BodyUserData userDataA, BodyUserData userDataB,
                                    Body bodyA, Body bodyB) {


    }

    /**
     * Handles chicken-related contacts
     */
    private void handleChickenContacts(BodyUserData userDataA, BodyUserData userDataB,
                                       Body bodyA, Body bodyB) {

        if (userDataA.getType() == ContactType.CHICKEN && userDataB.getType() == ContactType.CHICKEN) {
            Chicken.chickenContact(bodyA, userDataA.getId());
            bodyB.setLinearDamping(7f);
        }
    }

    /**
     * Handles bison-related contacts
     */

    /**
     * Handles player leaving door area
     */
    private void handlePlayerLeavingDoor(BodyUserData userDataA, BodyUserData userDataB) {
        boolean isPlayerDoorContact =
                (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) ||
                        (userDataB.getType() == ContactType.PLAYER && userDataA.getType() == ContactType.DOOR);

        if (isPlayerDoorContact) {
            BodyUserData doorData = (userDataA.getType() == ContactType.DOOR) ? userDataA : userDataB;
            Door door = DoorManager.getDoorById(doorData.getId());
            if (door != null) {
                System.out.println("Player ended contact with door: " + door.getName());
                door.playerLeave();
            }
        }
    }

    /**
     * Handles special sensor cases like ThicketSaint sensor
     */
    private void handleSpecialSensorCases(Fixture fixtureA, Fixture fixtureB) {
        // Handle ThicketSaint sensor contact
        // System.out.println("SPECIAL");
        if (fixtureA.getUserData() != null && "saintSensor".equals(fixtureA.getUserData())) {
            if (fixtureB.getUserData() instanceof BodyUserData) {
                handleSensorContact((BodyUserData) fixtureB.getUserData(), fixtureA.getUserData(), true);
                //System.out.println("YO");
            }
        }
        else if (fixtureB.getUserData() != null && fixtureB.getUserData() instanceof ThicketSaint) {
            if (fixtureA.getUserData() instanceof BodyUserData) {

                handleSensorContact((BodyUserData) fixtureA.getUserData(), fixtureB.getUserData(), true);
            }
        }
    }

    private void handleEndOfSpecialSensorCases(Fixture fixtureA, Fixture fixtureB) {
        // Handle ThicketSaint sensor contact

        if (fixtureA.getUserData() != null && "saintSensor".equals(fixtureA.getUserData())) {
            if (fixtureB.getUserData() instanceof BodyUserData) {
                handleSensorContact((BodyUserData) fixtureB.getUserData(), fixtureA.getUserData(), false);

            }
        }
        else if (fixtureB.getUserData() != null && fixtureB.getUserData() instanceof ThicketSaint) {
            if (fixtureA.getUserData() instanceof BodyUserData) {
                handleSensorContact((BodyUserData) fixtureA.getUserData(), fixtureB.getUserData(), false);
            }
        }
    }

    /**
     * Handle sensor contact cases
     */
    private void handleSensorContact(BodyUserData userData, Object otherEntity, Boolean pursuit) {
        if (userData.getType() == ContactType.PLAYER && otherEntity instanceof ThicketSaint) {
            ThicketSaint thicketSaint = (ThicketSaint) otherEntity;
            thicketSaint.setBeginPursuit(pursuit);
        }
        else if (userData.getType() == ContactType.NPC) {
            NPC npc = NPCManager.getNPCById(userData.getId());
            if (npc != null) {
                System.out.println("Player sensor detected NPC: " + userData.getId());
                // Maybe show a hint that player can interact with this NPC
            }
        }
    }
}