package ChordProtocol;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ModuloInteger {
    private final int _unModdedValue;
    private final int _moddedValue;
    private final int _modulus;

    private final Logger _logger;

    ModuloInteger(int unModdedValue, int modulus) {
        LogManager.getLogManager().reset();
        _logger = Logger.getLogger("ChordNode");

        _unModdedValue = unModdedValue;
        _modulus = modulus;
        _moddedValue = Math.floorMod(_unModdedValue, _modulus);
    }

    public int getUnModdedValue() { return _unModdedValue; }
    public int getModdedValue()   { return _moddedValue; }

    public boolean inRange(Inclusivity lowerBoundInclusivity, int lowerBound,
                            Inclusivity upperBoundInclusivity, int upperBound)
    {
        _logger.finest(String.format("COMMAND [ lowerBound = %s %d | upperBound = %s %d]",
                lowerBoundInclusivity, lowerBound, upperBoundInclusivity, upperBound));

        int value = _unModdedValue < 0 ? _moddedValue : _unModdedValue;

        if(upperBound <= lowerBound) upperBound += _modulus;
        _logger.finest(String.format("CORRECTED-BOUND [upperBound = %d]", upperBound));

        boolean lowerPredicate = lowerBoundInclusivity == Inclusivity.Inclusive ? value >= lowerBound : value > lowerBound;
        boolean upperPredicate = upperBoundInclusivity == Inclusivity.Inclusive ? value <= upperBound : value < upperBound;
        _logger.finest(String.format("PREDICATES [lowerPredicate = %b | upperPredicate = %b]", lowerPredicate, upperPredicate));

        return lowerPredicate && upperPredicate;
    }
}
