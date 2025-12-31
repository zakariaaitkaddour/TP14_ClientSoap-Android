package ma.projet.clientsoap_android_ksoap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.projet.clientsoap_android_ksoap.adapter.CompteAdapter;
import ma.projet.clientsoap_android_ksoap.beans.Compte;
import ma.projet.clientsoap_android_ksoap.beans.TypeCompte;
import ma.projet.clientsoap_android_ksoap.ws.Service;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private Button btnAdd;
    private CompteAdapter adapter;
    private Service service;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        service = new Service();
        adapter = new CompteAdapter();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadComptes();
    }

    /**
     * Initialise les vues.
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.fabAdd);
    }

    /**
     * Configure le RecyclerView.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(new CompteAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(final Compte compte) {
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle("Supprimer le compte")
                        .setMessage("Voulez-vous vraiment supprimer le compte n°" + compte.getId() + " ?")
                        .setPositiveButton("Supprimer", (dialog, which) -> {
                            deleteCompte(compte);
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });

        adapter.setOnEditClickListener(new CompteAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(Compte compte) {
                Toast.makeText(MainActivity.this,
                        "Modification du compte " + compte.getId(),
                        Toast.LENGTH_SHORT).show();
                // Implémentez ici la logique de modification si nécessaire
            }
        });
    }

    /**
     * Configure les listeners.
     */
    private void setupListeners() {
        btnAdd.setOnClickListener(v -> showAddCompteDialog());
    }

    /**
     * Affiche la boîte de dialogue pour ajouter un compte.
     */
    private void showAddCompteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.popup, null);

        final TextInputEditText etSolde = dialogView.findViewById(R.id.etSolde);
        final RadioButton radioCourant = dialogView.findViewById(R.id.radioCourant);

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Nouveau compte")
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    try {
                        String soldeText = etSolde.getText() != null ?
                                etSolde.getText().toString().trim() : "";

                        if (soldeText.isEmpty()) {
                            Toast.makeText(MainActivity.this,
                                    "Veuillez entrer un solde",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final double solde = Double.parseDouble(soldeText);

                        if (solde < 0) {
                            Toast.makeText(MainActivity.this,
                                    "Le solde ne peut pas être négatif",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final TypeCompte type = radioCourant.isChecked() ?
                                TypeCompte.COURANT : TypeCompte.EPARGNE;

                        createCompte(solde, type);

                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this,
                                "Veuillez entrer un nombre valide",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Crée un nouveau compte
     */
    private void createCompte(final double solde, final TypeCompte type) {
        Log.d(TAG, "Creating compte with solde=" + solde + ", type=" + type);

        executorService.execute(() -> {
            try {
                final boolean success = service.createCompte(solde, type);

                mainHandler.post(() -> {
                    if (success) {
                        Toast.makeText(MainActivity.this,
                                "Compte ajouté avec succès",
                                Toast.LENGTH_SHORT).show();
                        loadComptes();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Erreur lors de l'ajout du compte",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error creating compte", e);
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this,
                            "Erreur: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Supprime un compte
     */
    private void deleteCompte(final Compte compte) {
        Log.d(TAG, "Deleting compte with id=" + compte.getId());

        executorService.execute(() -> {
            try {
                final boolean success = service.deleteCompte(compte.getId());

                mainHandler.post(() -> {
                    if (success) {
                        adapter.removeCompte(compte);
                        Toast.makeText(MainActivity.this,
                                "Compte supprimé avec succès",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Erreur lors de la suppression du compte",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error deleting compte", e);
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this,
                            "Erreur: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Charge la liste des comptes depuis le service SOAP.
     */
    private void loadComptes() {
        Log.d(TAG, "Loading comptes...");

        executorService.execute(() -> {
            try {
                final List<Compte> comptes = service.getComptes();

                mainHandler.post(() -> {
                    if (comptes != null && !comptes.isEmpty()) {
                        adapter.updateComptes(comptes);
                        Log.d(TAG, "Loaded " + comptes.size() + " comptes");
                        Toast.makeText(MainActivity.this,
                                comptes.size() + " compte(s) chargé(s)",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "No comptes found");
                        Toast.makeText(MainActivity.this,
                                "Aucun compte trouvé",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading comptes", e);
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this,
                            "Erreur de connexion: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}