package com.example.finalar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.lullmodel.SkeletonDef;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.ScaleController;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
//        implements
//        FragmentOnAttachListener,
//        BaseArFragment.OnTapArPlaneListener,
//        BaseArFragment.OnSessionConfigurationListener,
//        ArFragment.OnViewCreatedListener

    private ArFragment arFragment;
//        private Renderable model;
//        private ViewRenderable viewRenderable;
       TextView arModelsTxt;
      private int clickNo = 0;

      private int modelNo = 0;
      private int classType = 0;

    public static boolean checkSystemSupport(Activity activity) {

        // checking whether the API version of the running Android >= 24
        // that means Android Nougat 7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            // checking whether the OpenGL version >= 3.0
            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportFragmentManager().addFragmentOnAttachListener(this);
//
//        if (savedInstanceState == null) {
//            if (Sceneform.isSupported(this)) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.arFragment, ArFragment.class, null)
//                        .commit();
//            }
//        }

//        loadModels();

        if(checkSystemSupport(this)){
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
            arModelsTxt = findViewById(R.id.arModels);
            Intent intent = getIntent();
            if(intent.getIntExtra("FURNITURE",0) != 0) classType = R.menu.furniture_models;
            else if(intent.getIntExtra("ANIMALS",0) != 0) classType = R.menu.animal_models;
            else if(intent.getIntExtra("CARS",0) != 0) classType = R.menu.car_models;
            else if(intent.getIntExtra("WATCH",0) != 0) classType = R.menu.watch_models;
            arModelsTxt.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showPopUp(v,classType);
                    return true;
                }
            });
            assert arFragment != null;
            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

                clickNo++;

                // the 3d model comes to the scene only the first time we tap the screen
//                if (clickNo == 1) {
                Anchor anchor = hitResult.createAnchor();
                ModelRenderable.builder()
                        .setSource(this, modelNo)
                        .setIsFilamentGltf(true)
                        .build()
                        .thenAccept(modelRenderable -> addModel(anchor, modelRenderable))
                        .exceptionally(throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Something is not right" + throwable.getMessage()).show();
                            return null;
                        });
//                }
            });

        }
        else {
            return;
        }
    }

    private void addModel(Anchor anchor, ModelRenderable modelRenderable) {

          // Creating a AnchorNode with a specific anchor
          AnchorNode anchorNode = new AnchorNode(anchor);



          anchorNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

          // attaching the anchorNode with the ArFragment
          anchorNode.setParent(arFragment.getArSceneView().getScene());

          // attaching the anchorNode with the TransformableNode
          TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
          model.setParent(anchorNode);
          ScaleController scaleController = model.getScaleController();
          model.getScaleController().setMinScale(0.1f);
          model.getScaleController().setMaxScale(3.0f);

          // attaching the 3d model with the TransformableNode
          // that is already attached with the node
          model.setRenderable(modelRenderable);
          model.select();
    }
    public void showPopUp(View v, int classType){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.inflate(classType);
        popupMenu.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.ferrari:
                modelNo = R.raw.ferrari;
                arModelsTxt.setText("Ferrari");
                return true;
            case R.id.porche:
                modelNo = R.raw.triumph_spitfire_mkiii;
                arModelsTxt.setText("Porche");
                return true;
            case R.id.audi:
                modelNo = R.raw.audi;
                arModelsTxt.setText("Audi");
                return true;
            case R.id.bmw:
                modelNo = R.raw.bmw;
                arModelsTxt.setText("BMW");
                return true;
            case R.id.supra:
                modelNo = R.raw.toyota_supra;
                arModelsTxt.setText("Toyota Supra");
                return true;
            case R.id.couch1:
                modelNo = R.raw.couch;
                arModelsTxt.setText("Couch1");
                return true;
            case R.id.couch2:
                modelNo = R.raw.couch_2;
                arModelsTxt.setText("Couch2");
                return true;
            case R.id.chester_field_sofa:
                modelNo = R.raw.chesterfield_sofa;
                arModelsTxt.setText("Chesterfield Sofa");
                return true;
            case R.id.foot_stool:
                modelNo = R.raw.foot_stool;
                arModelsTxt.setText("Foot Stool");
                return true;
            case R.id.chair:
                modelNo = R.raw.chair;
                arModelsTxt.setText("Chair");
                return true;
            case R.id.designer_chair:
                modelNo = R.raw.designer_chair;
                arModelsTxt.setText("Designer Chair");
                return true;
            case R.id.office_desk:
                modelNo = R.raw.office_desk;
                arModelsTxt.setText("Office Desk");
                return true;
            case R.id.table_chair:
                modelNo = R.raw.table_chair;
                arModelsTxt.setText("Table Chair");
                return true;
            default:
                return false;
        }
    }

//    public void loadModels() {
//        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
//        ModelRenderable.builder()
//                .setSource(this, R.raw.designer_chair)
//                .setIsFilamentGltf(true)
//                .setAsyncLoadEnabled(true)
//                .build()
//                .thenAccept(model -> {
//                    MainActivity activity = weakActivity.get();
//                    if (activity != null) {
//                        activity.model = model;
//                    }
//                })
//                .exceptionally(throwable -> {
//                    Toast.makeText(
//                            this, "Unable to load model", Toast.LENGTH_LONG).show();
//                    return null;
//                });
//        ViewRenderable.builder()
//                .setView(this, R.layout.view_model_title)
//                .build()
//                .thenAccept(viewRenderable -> {
//                    MainActivity activity = weakActivity.get();
//                    if (activity != null) {
//                        activity.viewRenderable = viewRenderable;
//                    }
//                })
//                .exceptionally(throwable -> {
//                    Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show();
//                    return null;
//                });
//    }


//    @Override
//    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
//        if (fragment.getId() == R.id.arFragment) {
//            arFragment = (ArFragment) fragment;
//            arFragment.setOnSessionConfigurationListener(this);
//            arFragment.setOnViewCreatedListener(this);
//            arFragment.setOnTapArPlaneListener(this);
//        }
//    }
//
//    @Override
//    public void onViewCreated(ArSceneView arSceneView) {
//        arFragment.setOnViewCreatedListener(null);
//
//        // Fine adjust the maximum frame rate
//        arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL);
//    }
//
//    @Override
//    public void onSessionConfiguration(Session session, Config config) {
//        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
//            config.setDepthMode(Config.DepthMode.AUTOMATIC);
//        }
//    }

//    @Override
//    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
//        if (model == null || viewRenderable == null) {
//            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create the Anchor.
//        Anchor anchor = hitResult.createAnchor();
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        anchorNode.setParent(arFragment.getArSceneView().getScene());
//
//        // Create the transformable model and add it to the anchor.
//        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
//        model.setParent(anchorNode);
//        model.setRenderable(this.model)
//                .animate(true).start();
//        model.select();
//
//        Node titleNode = new Node();
//        titleNode.setParent(model);
//        titleNode.setEnabled(false);
//        titleNode.setLocalPosition(new Vector3(0.0f, 1.0f, 0.0f));
//        titleNode.setRenderable(viewRenderable);
//        titleNode.setEnabled(true);
//    }
}