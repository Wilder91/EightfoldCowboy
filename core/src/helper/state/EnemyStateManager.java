package helper.state;

import com.badlogic.gdx.math.Vector2;
import objects.GameEntity;

public class EnemyStateManager extends EntityStateManager<GameEntity, GameEntity.State> {


    @Override
    protected GameEntity.State getCurrentState(GameEntity entity) {
        return null;
    }

    @Override
    protected void setCurrentState(GameEntity entity, GameEntity.State newState) {

    }

    @Override
    protected StateHandler<GameEntity, GameEntity.State> getHandlerForState(GameEntity.State state) {
        return null;
    }
}