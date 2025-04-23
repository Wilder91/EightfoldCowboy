// Base EntityStateManager that can be used for any entity with states
package helper.state;

public abstract class EntityStateManager<E, S extends Enum<S>> {

    // Generic interface for state handlers
    public interface StateHandler<E, S> {
        void update(E entity, float delta);
        void enter(E entity);
        void exit(E entity);
    }

    public void update(E entity, float delta) {
        S currentState = getCurrentState(entity);
        StateHandler<E, S> handler = getHandlerForState(currentState);

        if (handler != null) {
            handler.update(entity, delta);
        }
    }

    // Abstract methods that need implementation in subclasses
    protected abstract S getCurrentState(E entity);
    protected abstract void setCurrentState(E entity, S newState);
    protected abstract StateHandler<E, S> getHandlerForState(S state);

    // Method to handle state updates
    public void updateState(E entity, float delta) {
        S currentState = getCurrentState(entity);
        StateHandler<E, S> handler = getHandlerForState(currentState);
        if (handler != null) {
            handler.update(entity, delta);
        }
    }

    // Method to change states
    public void changeState(E entity, S newState) {
        S currentState = getCurrentState(entity);
        if (currentState == newState) return;

        StateHandler<E, S> oldHandler = getHandlerForState(currentState);
        if (oldHandler != null) {
            oldHandler.exit(entity);
        }

        setCurrentState(entity, newState);

        StateHandler<E, S> newHandler = getHandlerForState(newState);
        if (newHandler != null) {
            newHandler.enter(entity);
        }
    }
}