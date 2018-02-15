package org.synogen.ftlautosave.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.synogen.ftlautosave.App;
import org.synogen.ftlautosave.BackupSave;
import org.synogen.ftlautosave.Util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML
    private ListView<BackupSave> savesList;

    @FXML
    public void initialize() throws IOException {
        Path savePath = Paths.get(App.config.getFtlSavePath());
        DirectoryStream<Path> savefiles = Files.newDirectoryStream(savePath, entry -> {
            if (entry.getFileName().toString().startsWith(App.config.getSavefile() + ".")) {
                return true;
            }
            return false;
        });
        DirectoryStream<Path> profiles = Files.newDirectoryStream(savePath, entry -> {
            if (entry.getFileName().toString().startsWith(App.config.getProfile() + ".")) {
                return true;
            }
            return false;
        });

        //convert iterators to list due to error message saying they have the same iterator when looping one iterator inside of the other
        List<Path> savefilesList = new ArrayList<Path>();
        savefiles.forEach(savefilesList::add);
        List<Path> profilesList = new ArrayList<Path>();
        profiles.forEach(profilesList::add);

        for (Path savefile : savefilesList) {
            for (Path profile : profilesList) {
                //find a profile with a timestamp that matches closely
                Instant saveTime = Util.getTimestamp(savefile.getFileName().toString());
                Instant profileTime = Util.getTimestamp(profile.getFileName().toString());

                if (Math.abs(saveTime.until(profileTime, ChronoUnit.MILLIS)) < 500) {
                    savesList.getItems().add(new BackupSave(savefile, profile));
                }
            }
        }
        savesList.getItems().sort(Collections.reverseOrder(new SortBackupSaves()));
    }

    private class SortBackupSaves implements Comparator<BackupSave> {

        @Override
        public int compare(BackupSave o1, BackupSave o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    @FXML
    private void restoreAndStartFtl(ActionEvent event) {

    }

}