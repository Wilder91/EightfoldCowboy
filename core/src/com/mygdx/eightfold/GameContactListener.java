package com.mygdx.eightfold;


import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.ConversationManager;
import helper.BodyUserData;
import helper.ContactType;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.animals.object_helper.BisonManager;
import objects.animals.object_helper.DoorManager;
import objects.inanimate.Door;

public class GameContactListener implements ContactListener {

    private ScreenInterface screenInterface;

    public GameContactListener(ScreenInterface screenInterface) {
        this.screenInterface = screenInterface;
    }

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
                System.out.println("BUG CONTACT");
            }
            if(userDataA.getType() ==ContactType.PLAYER && userDataB.getType() == ContactType.DOOR){
               // System.out.println(userDataB);
                Door door = DoorManager.getDoorById(userDataB.getId());
               // System.out.println("Player began contact with door: " + door.getName());
                door.playerContact();
            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BISON) {
                Bison bison = BisonManager.getBisonById(userDataB.getId());
                Player player = screenInterface.getPlayer();
                //ConversationManager conversationManager = new ConversationManager(1, bison, player, screenInterface);
                //conversationManager.startFirstLevelConversation();
                if(!bison.talkingBison) {
                    bison.playContactSound();
                    bison.isContacted = true;
                }else{

                if(bodyA.getPosition().x < bodyB.getPosition().x){

                    bison.isContacted = true;
                    if(bison.talkingBison){
                        b.getBody().setLinearVelocity(0,0);
                        b.getBody().setAngularVelocity(0);
                    }

                }
                }


            } else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
                Bison bison =  BisonManager.getBisonById(userDataB.getId());


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

            }
            if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.BIRD){
                Bird.playerContact(bodyB, userDataB.getId());

            }
            if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BIRD) {
                Bird.playerContact(bodyB, userDataB.getId());

            } else if (userDataA.getType() == ContactType.BIRD && userDataB.getType() == ContactType.BISON) {
                Bird.playerContact(a.getBody(), userDataA.getId());
                bodyB.setLinearDamping(7f);

            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.BIRD) {
                bodyA.setLinearDamping(7f);
                Bird.playerContact(b.getBody(), userDataB.getId());
            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.BISON) {
               bodyA.setLinearDamping(7f);
               bodyB.setLinearDamping(7f);
                Bison bison =  BisonManager.getBisonById(userDataA.getId());
                bison.playContactSound();

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
                        bison.endPlayerContact();
                        b.getBody().setLinearVelocity(0,0);
                        b.getBody().setAngularVelocity(0);
                    }
                    bison.endPlayerContact();
                    bison.playerContact(b.getBody(), b.getBody().getLinearVelocity());
                }
            }else if (userDataA.getType() == ContactType.PLAYER && userDataB.getType() == ContactType.DOOR) {
                Door door = DoorManager.getDoorById(userDataB.getId());
               System.out.println("Player ended contact with door: " + door.getName());
                door.playerLeave();
            } else if (userDataA.getType() == ContactType.DOOR && userDataB.getType() == ContactType.PLAYER) {
                Door door = DoorManager.getDoorById(userDataA.getId());
               System.out.println("Player ended contact with door: " + door.getName());
                door.playerLeave();
            }else if (userDataA.getType() == ContactType.BISON && userDataB.getType() == ContactType.PLAYER) {
                Bison bison = BisonManager.getBisonById(userDataA.getId());
                if (bison == null) {
                    System.out.println("Bison is null for ID: " + userDataA.getId());
                } else {
                    bison.playerContact(a.getBody(), a.getBody().getLinearVelocity());
                }

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