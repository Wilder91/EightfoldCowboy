package com.mygdx.eightfold;

import com.badlogic.gdx.physics.box2d.*;
import helper.BodyUserData;
import helper.ContactType;
import objects.animals.Bird;
import objects.animals.bison.Bison;
import objects.animals.helper.BisonManager;

public class GameContactListener implements ContactListener {

    private com.mygdx.eightfold.GameScreen gameScreen;

    public GameContactListener(com.mygdx.eightfold.GameScreen gameScreen) {
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
                System.out.println("Contact detected between player and bison");
                System.out.println("Player ID: " + userDataA.getId() + ", Bison ID: " + userDataB.getId());
                System.out.println("Bison ID: " + ((BodyUserData) b.getUserData()).getId());
                System.out.println("bison userData:" + userDataB.getId());
                Bison bison =  BisonManager.getBisonById(userDataB.getId());
                System.out.println(bison.getSprite());
                //bison.getSprite().flip(true,false);
                Bison.playerContact(b.getBody(), userDataB.getId());
                System.out.println(userDataB.getBody());

            } else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
//                System.out.println("Contact detected between player and bison");
//                System.out.println("Player ID: " + userDataB.getId() + ", Bison ID: " + userDataA.getId());
                Bison.playerContact(a.getBody(), userDataA.getId());
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
        // Implement as needed
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
        System.out.println(a.getUserData());
        return (a.getUserData() instanceof Bison && b.getUserData() instanceof Bison);

    }
}