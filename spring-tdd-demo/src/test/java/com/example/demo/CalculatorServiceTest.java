package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    @Test
    void add_shouldReturnSum_ofTwoIntegers() {
        CalculatorService calculatorService = new CalculatorService();
        int result = calculatorService.add(1, 2);
        assertEquals(3, result);
    }
}