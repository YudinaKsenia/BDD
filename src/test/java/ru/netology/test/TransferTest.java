package ru.netology.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class TransferTest {
    DashboardPage dashboardPage;
    CardInfo firstCardInfo;
    CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setup () {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        int amount = firstCardBalance/5;

        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;

        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.validTransfer(String.valueOf(amount), firstCardInfo);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        assertAll(() -> assertEquals(expectedFirstCardBalance, actualFirstCardBalance),
                  () -> assertEquals(expectedSecondCardBalance, actualSecondCardBalance));
    }

    @Test
    void shouldTransferFromSecondToFirst() {

            int amount = secondCardBalance/2;

            var expectedFirstCardBalance = firstCardBalance + amount;
            var expectedSecondCardBalance = secondCardBalance - amount;

            var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
            dashboardPage = transferPage.validTransfer(String.valueOf(amount), secondCardInfo);

            var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
            var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

            assertAll(() -> assertEquals(expectedFirstCardBalance, actualFirstCardBalance),
                () -> assertEquals(expectedSecondCardBalance, actualSecondCardBalance));
        }

    @Test
    void shouldNotTransferIfAmountMoreThanBalance() {
        int amount = firstCardBalance + 100;

        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;

        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.validTransfer(String.valueOf(amount), firstCardInfo);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        assertAll(() -> transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания"),
                () -> dashboardPage.checkCardBalance(firstCardInfo, firstCardBalance),
                () -> dashboardPage.checkCardBalance(secondCardInfo, secondCardBalance));
    }
}