package me.shreyeschekuru.dsa.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import me.shreyeschekuru.dsa.components.appnav.AppNav;
import me.shreyeschekuru.dsa.components.appnav.AppNavItem;
import me.shreyeschekuru.dsa.data.entity.User;
import me.shreyeschekuru.dsa.security.AuthenticatedUser;
import me.shreyeschekuru.dsa.views.addressform.AddressFormView;
import me.shreyeschekuru.dsa.views.cardlist.CardListView;
import me.shreyeschekuru.dsa.views.checkoutform.CheckoutFormView;
import me.shreyeschekuru.dsa.views.collaborativemasterdetail.CollaborativeMasterDetailView;
import me.shreyeschekuru.dsa.views.dashboard.DashboardView;
import me.shreyeschekuru.dsa.views.gridwithfilters.GridwithFiltersView;
import me.shreyeschekuru.dsa.views.imagelist.ImageListView;
import me.shreyeschekuru.dsa.views.masterdetail.MasterDetailView;
import me.shreyeschekuru.dsa.views.personform.PersonFormView;
import me.shreyeschekuru.dsa.views.richtexteditor.RichTextEditorView;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Debate Event Platform");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        // Starting with v24.1, AppNav will be replaced with the official
        // SideNav component.
        AppNav nav = new AppNav();

        if (accessChecker.hasAccess(DashboardView.class)) {
            nav.addItem(new AppNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_AREA_SOLID.create()));

        }
        if (accessChecker.hasAccess(GridwithFiltersView.class)) {
            nav.addItem(new AppNavItem("Grid with Filters", GridwithFiltersView.class,
                    LineAwesomeIcon.FILTER_SOLID.create()));

        }
        if (accessChecker.hasAccess(MasterDetailView.class)) {
            nav.addItem(
                    new AppNavItem("Master-Detail", MasterDetailView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(PersonFormView.class)) {
            nav.addItem(new AppNavItem("Person Form", PersonFormView.class, LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(AddressFormView.class)) {
            nav.addItem(
                    new AppNavItem("Address Form", AddressFormView.class, LineAwesomeIcon.MAP_MARKER_SOLID.create()));

        }
        if (accessChecker.hasAccess(ImageListView.class)) {
            nav.addItem(new AppNavItem("Image List", ImageListView.class, LineAwesomeIcon.TH_LIST_SOLID.create()));

        }
        if (accessChecker.hasAccess(CheckoutFormView.class)) {
            nav.addItem(new AppNavItem("Checkout Form", CheckoutFormView.class, LineAwesomeIcon.CREDIT_CARD.create()));

        }
        if (accessChecker.hasAccess(CardListView.class)) {
            nav.addItem(new AppNavItem("Card List", CardListView.class, LineAwesomeIcon.LIST_SOLID.create()));

        }
        if (accessChecker.hasAccess(CollaborativeMasterDetailView.class)) {
            nav.addItem(new AppNavItem("Collaborative Master-Detail", CollaborativeMasterDetailView.class,
                    LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(RichTextEditorView.class)) {
            nav.addItem(new AppNavItem("Rich Text Editor", RichTextEditorView.class, LineAwesomeIcon.EDIT.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
