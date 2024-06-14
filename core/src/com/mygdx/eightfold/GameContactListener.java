package com.mygdx.eightfold;


import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyUserData;
import helper.ContactType;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.animals.object_helper.BisonManager;
import objects.animals.object_helper.DoorManager;
import objects.inanimate.Door;

public class GameContactListener implements ContactListener {

    private GameScreen gameScreen;

    public GameContactListener(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        // Check if one of the fixtures has userData of type BodyUserData

        if (a.getUserData() instanceof BodyUserData && b.getUserData() instanceof BodyUserData) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            BodyUserData userDataB = (BodyUserData) b.getUserData();
            if(userDataA.getType() ==ContactType.PLAYER){
                System.out.println(userDataB.getType());
            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BISON) {
                 Bison bison = BisonManager.getBisonById(userDataB.getId());
                 bison.isContacted = true;
                 if(bison.talkingBison){
                     b.getBody().setLinearVelocity(0,0);
                     b.getBody().setAngularVelocity(0);
                 }

            } else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
                Bison bison =  BisonManager.getBisonById(userDataB.getId());


            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) {
                System.out.println(DoorManager.getDoorMap());
                Door door = DoorManager.getDoorById(1);
                door.playerContact();



            } else if (userDataA.getType() == ContactType.DOOR && userDataB.getType() == ContactType.PLAYER) {

                System.out.println("YOYOOYOYO");
            }
            if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BIRD) {

                a.getBody().setLinearDamping(2f);
                b.getBody().setLinearDamping(2f);

            } else if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BISON) {
                Bird.playerContact(a.getBody(), userDataA.getId());
                b.getBody().setLinearDamping(7f);

            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.BIRD) {
                a.getBody().setLinearDamping(7f);
                Bird.playerContact(b.getBody(), userDataB.getId());
            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.BISON) {
                a.getBody().setLinearDamping(7f);
                b.getBody().setLinearDamping(7f);

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
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BISON) {
                Bison bison = BisonManager.getBisonById(userDataB.getId());
                if (bison == null) {
                    System.out.println("Bison is null for ID: " + userDataB.getId());
                } else {
                    if(bison.talkingBison){
                        b.getBody().setLinearVelocity(0,0);
                        b.getBody().setAngularVelocity(0);
                    }
                    bison.playerContact(b.getBody(), b.getBody().getLinearVelocity());
                }
            } else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
                Bison bison = BisonManager.getBisonById(userDataA.getId());
                if (bison == null) {
                    System.out.println("Bison is null for ID: " + userDataA.getId());
                } else {
                    bison.playerContact(a.getBody(), a.getBody().getLinearVelocity());
                }
            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) {
                System.out.println(DoorManager.getDoorMap());
                Door door = DoorManager.getDoorById(1);
                //door.playerContact();



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

    private boolean isBisonContact(Fixture a, Fixture b) {
        //System.out.println(a.getUserData());
        return (a.getUserData() instanceof Bison && b.getUserData() instanceof Bison);

    }
}