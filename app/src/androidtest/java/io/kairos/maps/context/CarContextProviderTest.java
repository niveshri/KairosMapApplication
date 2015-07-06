package io.kairos.maps.context;

import junit.framework.TestCase;

import org.mockito.Matchers;

import java.util.LinkedHashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CarContextProviderTest extends TestCase {
    private CarContextProvider carContextProvider;
    private CarStatePredictor carStatePredictor;

    public void setUp() throws Exception {
        carStatePredictor = mock(CarStatePredictor.class);
        carContextProvider = new CarContextProvider(carStatePredictor);
    }

    public void testOnSense() throws Exception {
        CarContextListener mockCarListener = mock(CarContextListener.class);
        DriverLoadListener mockDriverListener = mock(DriverLoadListener.class);

        carContextProvider.requestCarStates(mockCarListener);
        carContextProvider.requestDriverLoad(mockDriverListener);

        when(carStatePredictor.predict(Matchers.<LinkedHashMap<String, Double>>any()))
                .thenReturn(CarState.CRUISING_HIGH_SPEED);
        carContextProvider.onSense(new LinkedHashMap<String, Double>());

        verify(mockCarListener).onCarStateReceived(CarState.CRUISING_HIGH_SPEED);
        verify(mockCarListener).onCarStateChanged(CarState.CRUISING_HIGH_SPEED);
        verify(mockDriverListener).onDriverLoadReceived(DriverLoad.LOW);
        verify(mockDriverListener).onDriverLoadChanged(DriverLoad.LOW);
        reset(mockCarListener, mockDriverListener);

        carContextProvider.onSense(new LinkedHashMap<String, Double>());

        verify(mockCarListener).onCarStateReceived(CarState.CRUISING_HIGH_SPEED);
        verify(mockCarListener, never()).onCarStateChanged(CarState.CRUISING_HIGH_SPEED);
        verify(mockDriverListener).onDriverLoadReceived(DriverLoad.LOW);
        verify(mockDriverListener, never()).onDriverLoadChanged(DriverLoad.LOW);
        reset(mockCarListener, mockDriverListener);

        when(carStatePredictor.predict(Matchers.<LinkedHashMap<String, Double>>any()))
                .thenReturn(CarState.DE_ACCELERATING);
        carContextProvider.onSense(new LinkedHashMap<String, Double>());

        verify(mockCarListener).onCarStateReceived(CarState.DE_ACCELERATING);
        verify(mockCarListener).onCarStateChanged(CarState.DE_ACCELERATING);
        verify(mockDriverListener).onDriverLoadReceived(DriverLoad.HIGH);
        verify(mockDriverListener).onDriverLoadChanged(DriverLoad.HIGH);
    }
}
