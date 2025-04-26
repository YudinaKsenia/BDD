package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.checkerframework.checker.units.qual.C;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement amountInput = $("[data-test-id='amount'] input");
    private SelenideElement fromInput = $("[data-test-id='from'] input");
    private final SelenideElement errorMessage = $("[data-test-id='error-notification'] .notification__content");

    public void makeTransfer(String amount, DataHelper.CardInfo cardInfo) {
        amountInput.setValue(amount);
        fromInput.setValue(cardInfo.getNumber());
        transferButton.click();
    }

    public DashboardPage validTransfer(String amount, DataHelper.CardInfo cardInfo) {
        makeTransfer(amount, cardInfo);
        return new DashboardPage();
    }
    public void findErrorMessage(String expectedText) {
        errorMessage.shouldBe(Condition.visible).shouldHave(Condition.text(expectedText));
    }
}