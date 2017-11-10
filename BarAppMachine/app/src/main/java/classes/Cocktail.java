package classes;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Created by Lucas on 01/11/2017.
 */

public class Cocktail implements List<BoissonQuantite>{

    ArrayList<BoissonQuantite> cocktail;

    public Cocktail() {
        cocktail = new ArrayList<BoissonQuantite>();
    }

    public int getPrix() {
        int prix = 0;
        for (BoissonQuantite bq : this.cocktail) {
            prix += bq.getBoisson().getPrix() * bq.getQuantite();
        }

        return prix;
    }

    public ArrayList<BoissonQuantite> getCocktail() {
        return cocktail;
    }

    public void setCocktail(ArrayList<BoissonQuantite> cocktail) {
        this.cocktail = cocktail;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {

        return false;
    }

    public boolean contains(BoissonQuantite aControler) {
        for (BoissonQuantite bq : this.getCocktail()) {
            if (bq.getBoisson().getId() == aControler.getBoisson().getId()) {
                return true;
            }
        }

        return false;
    }

    @NonNull
    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super BoissonQuantite> action) {

    }

    @NonNull
    @Override
    public BoissonQuantite[] toArray() {
        return new BoissonQuantite[0];
    }

    @Override
    public boolean add(BoissonQuantite o) {
        return this.cocktail.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<BoissonQuantite> operator) {

    }

    @Override
    public void sort(Comparator<? super BoissonQuantite> c) {

    }

    @Override
    public boolean addAll(@NonNull Collection collection) {
        return false;
    }

    @Override
    public boolean addAll(int i, @NonNull Collection collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public BoissonQuantite get(int i) {
        return this.cocktail.get(i);
    }

    @Override
    public BoissonQuantite set(int i, BoissonQuantite boissonQuantite) {
        return null;
    }

    @Override
    public void add(int i, BoissonQuantite boissonQuantite) {

    }

    @Override
    public BoissonQuantite remove(int i) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        BoissonQuantite bq = (BoissonQuantite) o;

        int aRetourner = 0;
        for (BoissonQuantite b : this.getCocktail()) {
            if (b.getBoisson().getId() == bq.getBoisson().getId()) {
                return aRetourner;
            }
            aRetourner++;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator listIterator(int i) {
        return null;
    }

    @NonNull
    @Override
    public List subList(int i, int i1) {
        return null;
    }

    @Override
    public Spliterator<BoissonQuantite> spliterator() {
        return null;
    }

    @Override
    public boolean retainAll(@NonNull Collection collection) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection collection) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection collection) {
        return false;
    }

    @NonNull
    @Override
    public Object[] toArray(@NonNull Object[] objects) {
        return new Object[0];
    }
}
