package com.mygdx.eightfold;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.ecs.EntityManager;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import helper.ContactType;
import objects.animals.birds.Bird;
import objects.animals.farm_animals.Chicken;
import objects.inanimate.inanimate_helpers.DoorManager;
import objects.humans.NPC;
import objects.humans.NPCManager;
import objects.inanimate.Door;

public class GameContactListener implements ContactListener {

    private ScreenInterface screenInterface;

    public GameContactListener(ScreenInterface screenInterface) {
        this.screenInterface = screenInterface;
    }
    int contactCounter = 0;
    @Override
    public void beginContact(Contact contact) {

        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        Body bodyA = a.getBody();
        Body bodyB = b.getBody();
        // Check if one of the fixtures has userData of type BodyUserData
        if (a.getUserData() instanceof BodyUserData && b.getUserData() instanceof BodyUserData) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            BodyUserData userDataB = (BodyUserData) b.getUserData();
            if (userDataB.getType() == ContactType.PLAYER && userDataA.getType() == ContactType.BUG){
                //System.out.println("BUG CONTACT");
            }
            if(userDataA.getType() ==ContactType.PLAYER && userDataB.getType() == ContactType.DOOR){

                Door door = DoorManager.getDoorById(userDataB.getId());
                //System.out.println("Door: " + door);
               // System.out.println("Player began contact with door: " + door.getName());
                door.playerContact();
            }
            if(userDataA.getType() ==ContactType.PLAYER && userDataB.getType() == ContactType.NPC){
                //System.out.println("user data: " + userDataB.getId());
                NPC npc = NPCManager.getNPCById(userDataB.getId());
                //screenInterface.getNPCById(userDataB.getId());
                //System.out.println(npc);

                // System.out.println("Player began contact with door: " + door.getName());
                npc.playerContact(npc);
            }

            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) {
                //System.out.println(DoorManager.getDoorMap());

                Door door = DoorManager.getDoorById(userDataB.getId());
               // System.out.println("Player began contact with door: " + door.getName());
                door.playerContact();




            } else if (userDataA.getType() == ContactType.DOOR && userDataB.getType() == ContactType.PLAYER) {
                //System.out.println(DoorManager.getDoorMap());
                Door door = DoorManager.getDoorById(userDataA.getId());
                door.playerContact();

            } else if (userDataA.getType() == ContactType.ATTACK && userDataB.getType() == ContactType.ENEMY) {
                //System.out.println("THATS A HIT");

            } else if (userDataA.getType() == ContactType.ENEMY && userDataB.getType() == ContactType.ATTACK) {
                contactCounter += 1;
                System.out.println(contactCounter);
                //System.out.println(userDataA);
                Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
                sound.play(.05f);
            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BIRD){
                Bird.playerContact(bodyB, userDataB.getId());

            }
            if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BIRD) {
                Bird.playerContact(bodyB, userDataB.getId());

            } else if (userDataA.getType() == ContactType.CHICKEN && userDataB.getType() == ContactType.CHICKEN) {
                Chicken.chickenContact(a.getBody(), userDataA.getId());
                bodyB.setLinearDamping(7f);

            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.BISON) {
                bodyA.setLinearDamping(7f);
                Bird.playerContact(b.getBody(), userDataB.getId());
            }

        }
        else if (b.getUserData() == "playerSensor" ) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            if (userDataA != null) {
                System.out.println("sensed" + userDataA);
            }
        }
    }



    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();


        // Check if both of the fixtures have userData of type BodyUserData
        if (a.getUserData() instanceof BodyUserData && b.getUserData() instanceof BodyUserData) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            BodyUserData userDataB = (BodyUserData) b.getUserData();


             if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) {
                Door door = DoorManager.getDoorById(userDataB.getId());
               System.out.println("Player ended contact with door: " + door.getName());
                door.playerLeave();
            } else if (userDataA.getType() == ContactType.DOOR && userDataB.getType() == ContactType.PLAYER) {
                Door door = DoorManager.getDoorById(userDataA.getId());
               System.out.println("Player ended contact with door: " + door.getName());
                door.playerLeave();
            } else if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.CHICKEN) {
                 //System.out.println("chi");


            }


        }else if (b.getUserData() == "playerSensor" ) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            if (userDataA != null) {
                System.out.println("sense over");
            }
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Implement as needed
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Implement as needed
    }


}