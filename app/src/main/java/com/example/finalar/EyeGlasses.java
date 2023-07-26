package com.example.finalar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EyeGlasses extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final Set<CompletableFuture<?>> loaders = new HashSet<>();

    private ArFrontFacingFragment arFragment;
    private ArSceneView arSceneView;

    private Texture faceTexture;
    private ModelRenderable faceModel;
    private int modelNo = 0;
    TextView lensModelsTxt;
    Button removeBtn;
    AugmentedFaceNode existingFaceNode;

    private final HashMap<AugmentedFace, AugmentedFaceNode> facesNodes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_glasses);

        getSupportFragmentManager().addFragmentOnAttachListener(this::onAttachFragment);
        lensModelsTxt = findViewById(R.id.lensModels);
        removeBtn = findViewById(R.id.removeBtn);
        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFrontFacingFragment.class, null)
                        .commit();
            }
        }
        lensModelsTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopup(v);
                return true;
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existingFaceNode != null) {
                    existingFaceNode.setParent(null);
                }
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
//        loadModels();
        loadTextures();
    }

    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragment) {
            arFragment = (ArFrontFacingFragment) fragment;
            arFragment.setOnViewCreatedListener(this::onViewCreated);
        }
    }

    public void onViewCreated(ArSceneView arSceneView) {
        this.arSceneView = arSceneView;

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        // Check for face detections
        arFragment.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
    }

    protected void onDestroy() {
        super.onDestroy();

        for (CompletableFuture<?> loader : loaders) {
            if (!loader.isDone()) {
                loader.cancel(true);
            }
        }
    }

    private void loadModels() {
        loaders.add(ModelRenderable.builder()
                .setSource(this, modelNo)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(model -> faceModel = model)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                }));
    }

    private void loadTextures() {
        loaders.add(Texture.builder()
                .setSource(this, R.drawable.empty)
                .setUsage(Texture.Usage.COLOR_MAP)
                .build()
                .thenAccept(texture -> faceTexture = texture)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load texture", Toast.LENGTH_LONG).show();
                    return null;
                }));
    }

    public void onAugmentedFaceTrackingUpdate(AugmentedFace augmentedFace) {
        if (faceModel == null || faceTexture == null) {
            return;
        }

        existingFaceNode = facesNodes.get(augmentedFace);

        switch (augmentedFace.getTrackingState()) {
            case TRACKING:
                if (existingFaceNode == null) {
                    AugmentedFaceNode faceNode = new AugmentedFaceNode(augmentedFace);

                    RenderableInstance modelInstance = faceNode.setFaceRegionsRenderable(faceModel);
                    modelInstance.setShadowCaster(false);
                    modelInstance.setShadowReceiver(true);

                    faceNode.setFaceMeshTexture(faceTexture);

                    arSceneView.getScene().addChild(faceNode);

                    facesNodes.put(augmentedFace, faceNode);
                }
                break;
            case STOPPED:
                if (existingFaceNode != null) {
                    arSceneView.getScene().removeChild(existingFaceNode);
                }
                facesNodes.remove(augmentedFace);
                break;
        }
    }


    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.inflate(R.menu.lens_models);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.lens1:

                modelNo = R.raw.g100;
                lensModelsTxt.setText("Lens 1");
                loadModels();
                return true;
            case R.id.lens2:

                modelNo = R.raw.g101;
                lensModelsTxt.setText("Lens 2");
                loadModels();
                return true;
            case R.id.lens3:

                modelNo = R.raw.g102;
                lensModelsTxt.setText("Lens 3");
                loadModels();
                return true;
            case R.id.lens4:

                modelNo = R.raw.g103;
                lensModelsTxt.setText("Lens 4");
                loadModels();
                return true;
            case R.id.lens5:

                modelNo = R.raw.g104;
                lensModelsTxt.setText("Lens 5");
                loadModels();
                return true;
            default:
                return false;
        }
    }
}