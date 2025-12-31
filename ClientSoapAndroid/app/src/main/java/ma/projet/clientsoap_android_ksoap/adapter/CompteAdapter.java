package ma.projet.clientsoap_android_ksoap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ma.projet.clientsoap_android_ksoap.R;
import ma.projet.clientsoap_android_ksoap.beans.Compte;

public class CompteAdapter extends RecyclerView.Adapter<CompteAdapter.CompteViewHolder> {
    private List<Compte> comptes = new ArrayList<>();

    // Interfaces pour gérer les clics
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(Compte compte);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Compte compte);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    /**
     * Met à jour la liste des comptes affichés.
     */
    public void updateComptes(List<Compte> newComptes) {
        comptes.clear();
        comptes.addAll(newComptes);
        notifyDataSetChanged();
    }

    /**
     * Supprime un compte de la liste.
     */
    public void removeCompte(Compte compte) {
        int position = comptes.indexOf(compte);
        if (position >= 0) {
            comptes.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public CompteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new CompteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompteViewHolder holder, int position) {
        holder.bind(comptes.get(position));
    }

    @Override
    public int getItemCount() {
        return comptes.size();
    }

    /**
     * Classe ViewHolder pour gérer les vues individuelles.
     */
    class CompteViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView solde;
        private Chip type;
        private TextView crDate;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public CompteViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.textId);
            solde = itemView.findViewById(R.id.textSolde);
            type = itemView.findViewById(R.id.textType);
            crDate = itemView.findViewById(R.id.textDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final Compte compte) {
            id.setText("Compte Numéro " + compte.getId());
            solde.setText(compte.getSolde() + " DH");
            type.setText(compte.getType().name());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            crDate.setText(sdf.format(compte.getDateCreation()));

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditClickListener != null) {
                        onEditClickListener.onEditClick(compte);
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick(compte);
                    }
                }
            });
        }
    }
}