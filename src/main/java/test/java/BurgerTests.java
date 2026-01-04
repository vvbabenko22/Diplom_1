package test.java;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Database;
import praktikum.Ingredient;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BurgerTests {

    private Burger burger;
    private static Database database;

    // Исходные данные для теста (различные варианты булочек и начинок)
    private Bun initialBun;
    private Ingredient firstIngredient;
    private Ingredient secondIngredient;

    // Параметры теста (названия булочек и ингредиентов, которые будут использоваться)
    private String expectedBunName;
    private String expectedFirstIngredientName;
    private String expectedSecondIngredientName;

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

    // Подготовительные действия перед каждым тестом
    @Before
    public void setup() {
        // Создаём пустой бургер
        burger = new Burger();

        // Добавляем ингредиенты
        burger.setBuns(initialBun);
        burger.addIngredient(firstIngredient);
        burger.addIngredient(secondIngredient);
    }

    // Список наборов данных для тестирования
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        database = new Database();

        return Arrays.asList(new Object[][]{
                // Белая булочка + острый соус + котлета
                {database.availableBuns().get(1), database.availableIngredients().get(0), database.availableIngredients().get(3),
                        "white bun", "hot sauce", "cutlet"},

                // Красная булочка + чили + динозаврик
                {database.availableBuns().get(2), database.availableIngredients().get(2), database.availableIngredients().get(4),
                        "red bun", "chili sauce", "dinosaur"}
        });
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

        // Замена оригинальной цены на округленную
        actualReceipt = actualReceipt.replaceAll("(?<=Price:\\s)\\d+(,\\d+)?", Long.toString(roundedPrice));

        // Очистка фактического чека от скрытых символов
        actualReceipt = actualReceipt.replaceAll("\r", "").trim();

        // Формируем ожидаемый чек динамически
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