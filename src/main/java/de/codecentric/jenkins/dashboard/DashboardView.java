package de.codecentric.jenkins.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.model.ViewGroup;
import jenkins.model.Jenkins;

/**
 * @author Miel Donkers (miel.donkers@codecentric.nl)
 */
public class DashboardView extends View {

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();


    public DashboardView(final String name) {
        super(name);
    }

    @DataBoundConstructor
    public DashboardView(final String name, final ViewGroup owner) {
        super(name, owner);
    }

    @Override
    public ViewDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * Gets all the items in this collection in a read-only view.
     */
    @Override
    public Collection<TopLevelItem> getItems() {
        return new ArrayList<TopLevelItem>();
    }

    /**
     * Checks if the job is in this collection.
     * @param item
     */
    @Override
    public boolean contains(final TopLevelItem item) {
        return false;
    }

    /**
     * Handles the configuration submission.
     * <p/>
     * Load view-specific properties here.
     * @param req
     */
    @Override
    protected void submit(final StaplerRequest req) throws IOException, ServletException, Descriptor.FormException {

    }

    /**
     * Creates a new {@link hudson.model.Item} in this collection.
     * <p/>
     * <p/>
     * This method should call {@link ModifiableItemGroup#doCreateItem(org.kohsuke.stapler.StaplerRequest, org.kohsuke.stapler.StaplerResponse)}
     * and then add the newly created item to this view.
     * @param req
     * @param rsp
     * @return null if fails.
     */
    @Override
    public Item doCreateItem(final StaplerRequest req, final StaplerResponse rsp) throws IOException, ServletException {
        return Jenkins.getInstance().doCreateItem(req, rsp);
    }

    public static class DescriptorImpl extends ViewDescriptor {

        /**
         * Returns the human-readable name of this type of view. Used
         * in the view creation screen. The string should look like
         * "Abc Def Ghi".
         */
        @Override
        public String getDisplayName() {
            return "DashboardView";
        }
    }

}
