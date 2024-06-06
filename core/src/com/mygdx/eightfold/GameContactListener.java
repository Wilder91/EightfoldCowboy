package com.mygdx.eightfold;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyUserData;
import helper.ContactType;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.animals.object_helper.BisonManager;

public class GameContactListener implements ContactListener {

    private GameScreen gameScreen;

    public GameContactListener(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (isBisonContact(a, b)){
            System.out.println("Contact between two bison");

        }
        // Check if one of the fixtures has userData of type BodyUserData
        if (a.getUserData() instanceof BodyUserData && b.getUserData() instanceof BodyUserData) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            BodyUserData userDataB = (BodyUserData) b.getUserData();

            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BISON) {
//                System.out.println("Contact detected between player and bison");
//                System.out.println("Player ID: " + userDataA.getId() + ", Bison ID: " + userDataB.getId());
//                System.out.println("Bison ID: " + ((BodyUserData) b.getUserData()).getId());
//                System.out.println("bison userData:" + userDataB.getId());

//                System.out.println(userDataB.getBody());
              // System.out.println("Contact body: " + b.getBody().getLinearVelocity());
            } else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
//                System.out.println("Contact detected between player and bison");
//                System.out.println("Player ID: " + userDataB.getId() + ", Bison ID: " + userDataA.getId());
                Bison bison =  BisonManager.getBisonById(userDataB.getId());


            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BIRD) {
//                System.out.println("Contact detected between player and bird");
//                System.out.println("Player ID: " + userDataA.getId() + ", Bird ID: " + userDataB.getId());
//                System.out.println(b.getBody());
                Bird.playerContact(userDataB.getBody(), userDataB.getId());
//                System.out.println(userDataB);

            } else if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.PLAYER) {
//                System.out.println("Contact detected between player and bird");
//                System.out.println("Player ID: " + userDataB.getId() + ", Bird ID: " + userDataA.getId());
                Bird.playerContact(a.getBody(), userDataA.getId());
            }
            if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BIRD) {
//                System.out.println("Contact detected between bird and bird");
//                System.out.println("Bird ID: " + userDataA.getId() + ", Bird ID: " + userDataB.getId());
                a.getBody().setLinearDamping(2f);
                b.getBody().setLinearDamping(2f);

            } else if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BISON) {
//                System.out.println("Contact detected between bird and bison");
//                System.out.println("Bird ID: " + userDataB.getId() + ", Bison ID: " + userDataA.getId());
                Bird.playerContact(a.getBody(), userDataA.getId());
                b.getBody().setLinearDamping(7f);

            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.BIRD) {
//                System.out.println("Contact detected between Bison and bird");
//                System.out.println("Bison ID: " + userDataB.getId() + ", Bird ID: " + userDataA.getId());
                a.getBody().setLinearDamping(7f);
                Bird.playerContact(b.getBody(), userDataB.getId());
            }

        }
    }



    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        // Check if one of the fixtures has userData of type BodyUserData
        if (a.getUserData() instanceof BodyUserData && b.getUserData() instanceof BodyUserData) {
            BodyUserData userDataA = (BodyUserData) a.getUserData();
            BodyUserData userDataB = (BodyUserData) b.getUserData();

            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BISON) {
                Bison bison = BisonManager.getBisonById(userDataB.getId());
//                System.out.println("Contact body velo: " + bison.getBody().getLinearVelocity().x);
//                System.out.println("Bison ID: " + userDataB.getId());
                if (bison == null) {
                    System.out.println("Bison is null for ID: " + userDataB.getId());
                } else {
                    bison.playerContact(b.getBody(), userDataB.getId(), b.getBody().getLinearVelocity());
                }
            } else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
                Bison bison = BisonManager.getBisonById(userDataA.getId());
                //System.out.println("Bison ID: " + userDataA.getId());
                if (bison == null) {
                    System.out.println("Bison is null for ID: " + userDataA.getId());
                } else {
                    bison.playerContact(a.getBody(), userDataA.getId(), a.getBody().getLinearVelocity());
                }
            }
            // Other conditions...
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