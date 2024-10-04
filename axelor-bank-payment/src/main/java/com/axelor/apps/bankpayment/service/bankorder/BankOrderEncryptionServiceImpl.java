package com.axelor.apps.bankpayment.service.bankorder;

import com.axelor.app.AppSettings;
import com.axelor.apps.bankpayment.exception.BankPaymentExceptionMessage;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.repo.TraceBackRepository;
import com.axelor.common.crypto.BytesEncryptor;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class BankOrderEncryptionServiceImpl implements BankOrderEncryptionService {

  protected MetaFiles metaFiles;

  @Inject
  public BankOrderEncryptionServiceImpl(MetaFiles metaFiles) {
    this.metaFiles = metaFiles;
  }

  /**
   * Encrypt bank order file according to axelor-config.properties settings
   *
   * @param file
   * @return
   * @throws IOException
   */
  @Override
  public File encryptFile(File file) throws AxelorException {
    BytesEncryptor encryptor = getEncryptor();
    try {
      byte[] encrypt = encryptor.encrypt(Files.readAllBytes(file.toPath()));

      Files.write(file.toPath(), encrypt, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

      return file;

    } catch (IOException e) {
      throw new AxelorException(
          e,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(BankPaymentExceptionMessage.BANK_ORDER_FILE_DECRYPT_ERROR));
    }
  }

  @Override
  public void checkInputPassword(String password) throws AxelorException {
    String encryptPassword = checkAndGetEncryptionPassword();
    if (!password.equals(encryptPassword)) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(BankPaymentExceptionMessage.BANK_ORDER_FILE_ENCRYPTION_INCORRECT_PASSWORD));
    }
  }

  @Override
  public byte[] getDecryptedBytes(File bankOrderFile) throws AxelorException {
    BytesEncryptor encryptor = getEncryptor();
    try {
      return encryptor.decrypt(Files.readAllBytes(bankOrderFile.toPath()));
    } catch (IOException e) {
      throw new AxelorException(
          e,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(BankPaymentExceptionMessage.BANK_ORDER_FILE_DECRYPT_ERROR));
    }
  }

  protected BytesEncryptor getEncryptor() throws AxelorException {
    AppSettings appSettings = AppSettings.get();
    String algorithm = appSettings.get("encryption.algorithm");
    String encryptPassword = checkAndGetEncryptionPassword();

    BytesEncryptor encryptor;

    if ("GCM".equalsIgnoreCase(algorithm)) {
      encryptor = BytesEncryptor.gcm(encryptPassword);
    } else {
      encryptor = BytesEncryptor.cbc(encryptPassword);
    }

    return encryptor;
  }

  /**
   * Check if the file is encrypted
   *
   * @param bankOrderGeneratedFile
   * @return
   * @throws IOException
   */
  @Override
  public boolean isFileEncrypted(MetaFile bankOrderGeneratedFile) throws AxelorException {
    if (bankOrderGeneratedFile == null) {
      return false;
    }
    File bankOrderFile = MetaFiles.getPath(bankOrderGeneratedFile).toFile();
    BytesEncryptor encryptor = getEncryptor();
    try {
      return encryptor.isEncrypted(Files.readAllBytes(bankOrderFile.toPath()));
    } catch (IOException e) {
      throw new AxelorException(
          e,
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(BankPaymentExceptionMessage.BANK_ORDER_FILE_DECRYPT_ERROR));
    }
  }

  @Override
  public String checkAndGetEncryptionPassword() throws AxelorException {
    String encryptPassword = AppSettings.get().get("encryption.bankorder.password");
    if (encryptPassword == null || encryptPassword.isEmpty()) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_INCONSISTENCY,
          I18n.get(BankPaymentExceptionMessage.BANK_ORDER_FILE_ENCRYPTION_NO_PASSWORD));
    }
    return encryptPassword;
  }
}
