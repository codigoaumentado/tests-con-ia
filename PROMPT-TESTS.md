# Prompt para que la IA escriba tests útiles (no inútiles)

Pégalo en Claude Code (o tu asistente) junto con la clase a probar:

```text
Escribe tests JUnit 5 + AssertJ para <Clase>. Reglas:
1. Cada test verifica un VALOR EXACTO (isEqualByComparingTo / isEqualTo), nunca solo "no es null".
2. Para cada regla de negocio, prueba el LÍMITE y el valor justo al otro lado
   (ej.: monto mínimo exacto vs. un centavo menos; último día válido vs. día siguiente).
3. Usa @ParameterizedTest con @CsvSource para casos de la misma regla.
4. Prueba los errores con assertThatThrownBy y el MENSAJE exacto.
5. Nada de mocks para objetos del dominio.
6. Al terminar, ejecuta `mvn test-compile org.pitest:pitest-maven:mutationCoverage`
   y agrega tests hasta que no sobreviva ninguna mutación.
```

La regla 6 es la clave: la cobertura dice qué líneas se ejecutaron; **PIT dice si tus tests detectan errores**.
