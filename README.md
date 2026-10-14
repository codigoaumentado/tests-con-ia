# Tests al 90 % con IA (sin tests inútiles)

Código del video del canal **Código Aumentado**.

| Tag | Tests | Cobertura (JaCoCo) | Mutation score (PIT) |
|---|---|---|---|
| `inutiles` | Solo verifican "no es null" | 100 % líneas · 100 % ramas | **43 %** (10/23) → el build falla |
| `utiles` | Valores exactos y límites | 100 % líneas · 100 % ramas | **100 %** (23/23) |

```bash
git checkout inutiles   # o utiles
mvn verify                                                  # tests + cobertura (mínimo 90 %)
mvn test-compile org.pitest:pitest-maven:mutationCoverage   # mutation testing (mínimo 85 %)
```

Reporte de PIT: `target/pit-reports/index.html`.

Prompt para que la IA escriba tests útiles: [PROMPT-TESTS.md](PROMPT-TESTS.md).

Stack: Java 21 · JUnit 5.14 · AssertJ 3.27 · JaCoCo 0.8.15 · PIT 1.30
