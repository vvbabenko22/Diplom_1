package test.java;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Database;
import praktikum.Ingredient;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BurgerTests {

    private Burger burger;

    // Исходные данные для теста (различные варианты булочек и начинок)
    private Bun initialBun;
    private Ingredient firstIngredient;
    private Ingredient secondIngredient;

    // Параметры теста (названия булочек и ингредиентов, которые будут использоваться)
    private String expectedBunName;
    private String expectedFirstIngredientName;
    private String expectedSecondIngredientName;

    // Мок базы данных
    @Mock
    private Database database;

    // Аннотация для автоматической инициализации моков
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Создаём пустой бургер
        burger = new Burger();

        // Подготовка мока базы данных
        prepareDatabaseMock();

        // Добавляем ингредиенты
        burger.setBuns(initialBun);
        burger.addIngredient(firstIngredient);
        burger.addIngredient(secondIngredient);
    }

    // Подготовка мока базы данных
    private void prepareDatabaseMock() {
        // Создаем заготовки булочек и ингредиентов
        Bun[] buns = new Bun[]{
                new Bun("black bun", 100),
                new Bun("white bun", 200),
                new Bun("red bun", 300)
        };

        Ingredient[] ingredients = new Ingredient[]{
                new Ingredient(praktikum.IngredientType.SAUCE, "hot sauce", 100),
                new Ingredient(praktikum.IngredientType.SAUCE, "sour cream", 200),
                new Ingredient(praktikum.IngredientType.SAUCE, "chili sauce", 300),
                new Ingredient(praktikum.IngredientType.FILLING, "cutlet", 100),
                new Ingredient(praktikum.IngredientType.FILLING, "dinosaur", 200),
                new Ingredient(praktikum.IngredientType.FILLING, "sausage", 300)
        };

        // Конфигурируем мок, чтобы он возвращал созданные булочки и ингредиенты
        when(database.availableBuns()).thenReturn(Arrays.asList(buns));
        when(database.availableIngredients()).thenReturn(Arrays.asList(ingredients));
    }

    // Список наборов данных для тестирования
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // Белая булочка + острый соус + котлета
                {new Bun("white bun", 200), new Ingredient(praktikum.IngredientType.SAUCE, "hot sauce", 100), new Ingredient(praktikum.IngredientType.FILLING, "cutlet", 100),
                        "white bun", "hot sauce", "cutlet"},

                // Красная булочка + чили + динозаврик
                {new Bun("red bun", 300), new Ingredient(praktikum.IngredientType.SAUCE, "chili sauce", 300), new Ingredient(praktikum.IngredientType.FILLING, "dinosaur", 200),
                        "red bun", "chili sauce", "dinosaur"}
        });
    }

    // Конструктор для передачи данных из параметризированного теста
    public BurgerTests(Bun initialBun, Ingredient firstIngredient, Ingredient secondIngredient,
                       String expectedBunName, String expectedFirstIngredientName, String expectedSecondIngredientName) {
        this.initialBun = initialBun;
        this.firstIngredient = firstIngredient;
        this.secondIngredient = secondIngredient;
        this.expectedBunName = expectedBunName;
        this.expectedFirstIngredientName = expectedFirstIngredientName;
        this.expectedSecondIngredientName = expectedSecondIngredientName;
    }

    // Тест 1: Проверка суммы стоимости бургера
    @Test
    public void testTotalPrice() {
        // Суммарная стоимость рассчитывается как удвоенная цена булочки плюс цены ингредиентов
        float expectedPrice = initialBun.getPrice() * 2 + firstIngredient.getPrice() + secondIngredient.getPrice();
        float actualPrice = burger.getPrice();

        // Проверяем, совпадает ли общая сумма
        assertEquals(expectedPrice, actualPrice, 0.01f);
    }

    // Тест 2: Проверка печати чека
    @Test
    public void testPrintReceipt() {
        // Получаем фактический чек
        String actualReceipt = burger.getReceipt();

        // Получаем итоговую цену и округляем её до целого числа
        float calculatedPrice = burger.getPrice();
        long roundedPrice = Math.round(calculatedPrice);

        // Замена оригинальной цены на округлённую
        actualReceipt = actualReceipt.replaceAll("(?<=Price:\\s)\\d+(,\\d+)?", Long.toString(roundedPrice));

        // Очистка фактического чека от скрытых символов
        actualReceipt = actualReceipt.replaceAll("\r", "").trim();

        // Формируем ожидаемый чек
        String expectedReceipt =
                "(==== " + expectedBunName + " ====)\n" +
                        "= " + firstIngredient.getType().toString().toLowerCase() + " " + expectedFirstIngredientName + " =\n" +
                        "= " + secondIngredient.getType().toString().toLowerCase() + " " + expectedSecondIngredientName + " =\n" +
                        "(==== " + expectedBunName + " ====)\n" +
                        "\n" +
                        "Price: " + roundedPrice;

        // Проверяем чек целиком
        assertEquals(expectedReceipt, actualReceipt);
    }

    // Тест 3: Проверка удаления ингредиента
    @Test
    public void testRemoveIngredient() {
        // Удаляем первый ингредиент
        burger.removeIngredient(0);

        // Осталась только котлета
        assertEquals(1, burger.ingredients.size());
        assertEquals(expectedSecondIngredientName, burger.ingredients.get(0).getName());
    }

    // Тест 4: Проверка перемещения ингредиента
    @Test
    public void testMoveIngredient() {
        // Меняем местами ингредиенты (переносим соус вперёд)
        burger.moveIngredient(0, 1);

        // Проверяем порядок ингредиентов
        assertEquals(expectedSecondIngredientName, burger.ingredients.get(0).getName());
        assertEquals(expectedFirstIngredientName, burger.ingredients.get(1).getName());
    }

}