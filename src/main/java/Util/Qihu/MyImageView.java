package Util.Qihu;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class MyImageView extends BorderPane {
    private ImageView imageView;

    public MyImageView(QihuPicBean qihuPicBean) {
        super();
        this.setBackground(new Background(new BackgroundFill(Color.web("#696969", 0.8), null, null)));
        this.imageView = new ImageView(new Image(qihuPicBean.getUrl()));
        this.setCenter(imageView);

        DoubleBinding binding = this.prefWidthProperty().multiply(0.8);
        imageView.fitWidthProperty().bind(binding);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
