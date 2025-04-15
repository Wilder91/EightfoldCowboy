package helper.state;

public class AnimalStateManager extends EntityStateManager{
    @Override
    protected Enum getCurrentState(Object entity) {
        return null;
    }

    @Override
    protected void setCurrentState(Object entity, Enum newState) {

    }

    @Override
    protected StateHandler getHandlerForState(Enum state) {
        return null;
    }
}
