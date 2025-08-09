/* Minimal SPA with LocalStorage-backed data */
(function () {
  const root = document.getElementById('appRoot');
  const accountLink = document.getElementById('accountLink');
  const sellerLink = document.getElementById('sellerLink');
  const cartBadge = document.getElementById('cartBadge');
  const yearEl = document.getElementById('year');
  const searchInput = document.getElementById('searchInput');
  const searchBtn = document.getElementById('searchBtn');

  if (yearEl) yearEl.textContent = new Date().getFullYear();

  // Data bootstrap
  const store = {
    products: [],
    users: [],
    reviews: [],
    orders: [],
    cart: [],
    session: { userId: null },
    seq: { product: 1, user: 1, review: 1, order: 1 }
  };

  function load() {
    const data = JSON.parse(localStorage.getItem('shoply') || 'null');
    if (!data) {
      seed();
      save();
      return store;
    }
    Object.assign(store, data);
    return store;
  }

  function save() {
    localStorage.setItem('shoply', JSON.stringify(store));
  }

  function seed() {
    // Users: customer and seller
    store.users = [
      { id: 1, email: 'alice@example.com', name: 'Alice', role: 'customer', password: 'pass' },
      { id: 2, email: 'seller@example.com', name: 'Seller', role: 'seller', password: 'pass' }
    ];
    store.session.userId = null;
    store.seq.user = 3;

    // Products
    const demo = [
      { title: 'Wireless Headphones', price: 79.99, category: 'Electronics', image: 'https://images.unsplash.com/photo-1518441902110-9f89e75fdb38?q=80&w=1200&auto=format&fit=crop', stock: 24 },
      { title: 'Smart Watch Series 5', price: 149.00, category: 'Wearables', image: 'https://images.unsplash.com/photo-1516574187841-cb9cc2ca948b?q=80&w=1200&auto=format&fit=crop', stock: 18 },
      { title: 'Ergonomic Office Chair', price: 229.00, category: 'Home & Office', image: 'https://images.unsplash.com/photo-1582582494700-9a63f9e6df08?q=80&w=1200&auto=format&fit=crop', stock: 12 },
      { title: 'Gaming Mouse Pro', price: 39.00, category: 'Electronics', image: 'https://images.unsplash.com/photo-1593642532400-2682810df593?q=80&w=1200&auto=format&fit=crop', stock: 40 },
      { title: 'Yoga Mat Eco', price: 25.00, category: 'Sports', image: 'https://images.unsplash.com/photo-1552196563-55cd4e45efb3?q=80&w=1200&auto=format&fit=crop', stock: 50 },
      { title: 'Stainless Water Bottle', price: 19.00, category: 'Outdoors', image: 'https://images.unsplash.com/photo-1515003197210-e0cd71810b5f?q=80&w=1200&auto=format&fit=crop', stock: 70 },
      { title: 'Bluetooth Speaker', price: 59.00, category: 'Electronics', image: 'https://images.unsplash.com/photo-1585386959984-a41552231658?q=80&w=1200&auto=format&fit=crop', stock: 33 },
      { title: 'Running Shoes', price: 89.00, category: 'Sports', image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1200&auto=format&fit=crop', stock: 26 }
    ];
    store.products = demo.map((p, i) => ({ id: i + 1, description: `${p.title} description`, rating: 4, ...p }));
    store.seq.product = demo.length + 1;
    store.reviews = [
      { id: 1, productId: 1, userId: 1, rating: 5, comment: 'Great sound and battery!' }
    ];
    store.seq.review = 2;
    store.orders = [];
    store.seq.order = 1;
    store.cart = [];
  }

  load();

  // Helpers
  function getCurrentUser() { return store.users.find(u => u.id === store.session.userId) || null; }
  function isSeller() { const u = getCurrentUser(); return !!u && u.role === 'seller'; }
  function formatPrice(n) { return `$${n.toFixed(2)}`; }
  function updateHeader() {
    const user = getCurrentUser();
    accountLink.textContent = user ? user.name : 'Login';
    accountLink.href = user ? '#profile' : '#auth';
    sellerLink.classList.toggle('hide', !isSeller());
    const count = store.cart.reduce((s, i) => s + i.qty, 0);
    cartBadge.textContent = count;
  }

  function navigate(hash) {
    if (!hash) hash = '#home';
    const route = hash.split('?')[0];
    if (route === '#home') renderHome();
    else if (route === '#catalog') renderCatalog();
    else if (route.startsWith('#product/')) {
      const id = Number(route.split('/')[1]);
      renderProduct(id);
    } else if (route === '#cart') renderCart();
    else if (route === '#checkout') renderCheckout();
    else if (route === '#confirm') renderConfirmation();
    else if (route === '#orders') renderOrders();
    else if (route === '#auth') renderAuth();
    else if (route === '#profile') renderProfile();
    else if (route === '#seller') renderSeller();
    else renderHome();
    updateHeader();
  }

  // Rendering
  function renderHome() {
    root.innerHTML = `
      <section class="panel stack">
        <div class="card center" style="height: 160px">
          <div>
            <h1>Discover products you love</h1>
            <p class="muted">Fast search, clear details, simple checkout.</p>
            <div class="row" style="justify-content:center;margin-top:8px;">
              <a href="#catalog" class="btn btn-primary">Shop now</a>
              <a href="#seller" class="btn btn-ghost">Sell on Shoply</a>
            </div>
          </div>
        </div>
        <div class="stack">
          <h3>Featured</h3>
          <div class="grid" id="featuredGrid"></div>
        </div>
      </section>
    `;
    const grid = document.getElementById('featuredGrid');
    grid.innerHTML = store.products.slice(0, 8).map(renderProductCard).join('');
    bindProductCardActions(grid);
  }

  function renderCatalog() {
    const categories = Array.from(new Set(store.products.map(p => p.category))).sort();
    root.innerHTML = `
      <section class="stack">
        <div class="row">
          <div class="panel" style="min-width:220px;max-width:260px;">
            <div class="form">
              <div class="field">
                <label>Category</label>
                <select id="filterCategory">
                  <option value="">All</option>
                  ${categories.map(c => `<option value="${c}">${c}</option>`).join('')}
                </select>
              </div>
              <div class="field">
                <label>Price up to</label>
                <input id="filterPrice" type="number" min="0" step="1" placeholder="No limit" />
              </div>
              <button id="applyFilters" class="btn">Apply</button>
            </div>
          </div>
          <div class="stack" style="flex:1">
            <div class="row" style="justify-content:space-between;align-items:center;">
              <h2>Catalog</h2>
              <span class="pill" id="resultsCount"></span>
            </div>
            <div class="grid" id="catalogGrid"></div>
          </div>
        </div>
      </section>
    `;

    const grid = document.getElementById('catalogGrid');
    const resultsCount = document.getElementById('resultsCount');
    const categoryEl = document.getElementById('filterCategory');
    const priceEl = document.getElementById('filterPrice');
    const applyBtn = document.getElementById('applyFilters');

    function refresh() {
      let list = [...store.products];
      const q = (searchInput && searchInput.value || '').trim().toLowerCase();
      if (q) list = list.filter(p => p.title.toLowerCase().includes(q));
      const cat = categoryEl.value;
      if (cat) list = list.filter(p => p.category === cat);
      const price = Number(priceEl.value);
      if (!Number.isNaN(price) && price > 0) list = list.filter(p => p.price <= price);
      resultsCount.textContent = `${list.length} results`;
      grid.innerHTML = list.map(renderProductCard).join('');
      bindProductCardActions(grid);
    }

    applyBtn.addEventListener('click', refresh);
    refresh();
  }

  function renderProductCard(p) {
    return `
      <div class="card product-card" data-id="${p.id}">
        <img src="${p.image}" alt="${p.title}" />
        <h4>${p.title}</h4>
        <div class="row" style="justify-content:space-between;align-items:center;">
          <span class="price">${formatPrice(p.price)}</span>
          <span class="rating">${'★'.repeat(Math.round(p.rating || 4))}${'☆'.repeat(5 - Math.round(p.rating || 4))}</span>
        </div>
        <div class="row" style="margin-top:8px;">
          <a class="btn" href="#product/${p.id}">View</a>
          <button class="btn btn-primary addToCart">Add</button>
        </div>
      </div>
    `;
  }

  function bindProductCardActions(container) {
    container.querySelectorAll('.addToCart').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = Number(e.target.closest('.product-card').dataset.id);
        addToCart(id, 1);
        updateHeader();
      });
    });
  }

  function renderProduct(id) {
    const p = store.products.find(x => x.id === id);
    if (!p) { root.innerHTML = '<div class="panel">Product not found.</div>'; return; }
    const productReviews = store.reviews.filter(r => r.productId === id);
    const avg = productReviews.length ? (productReviews.reduce((s, r) => s + r.rating, 0) / productReviews.length) : (p.rating || 0);
    root.innerHTML = `
      <section class="split">
        <div class="panel">
          <img src="${p.image}" alt="${p.title}" style="width:100%;border-radius:12px;max-height:420px;object-fit:cover;" />
        </div>
        <div class="stack">
          <div class="panel">
            <h2>${p.title}</h2>
            <div class="row" style="gap:8px;align-items:center;">
              <span class="price">${formatPrice(p.price)}</span>
              <span class="rating">${'★'.repeat(Math.round(avg))}${'☆'.repeat(5 - Math.round(avg))}</span>
              <span class="pill">In stock: ${p.stock}</span>
            </div>
            <div class="row" style="margin-top:8px;">
              <input id="qty" type="number" min="1" value="1" style="width:90px" />
              <button id="addBtn" class="btn btn-primary">Add to Cart</button>
            </div>
            <p class="muted" style="margin-top:12px;">${p.description}</p>
          </div>
          <div class="panel">
            <div class="row" style="justify-content:space-between;align-items:center;">
              <h3>Reviews</h3>
              <button id="writeReview" class="btn">Write Review</button>
            </div>
            <div id="reviewsList" class="stack" style="margin-top:8px;"></div>
          </div>
        </div>
      </section>
    `;
    document.getElementById('addBtn').addEventListener('click', () => {
      const qty = Math.max(1, Number(document.getElementById('qty').value) || 1);
      addToCart(id, qty);
      updateHeader();
    });

    document.getElementById('writeReview').addEventListener('click', () => {
      const user = getCurrentUser();
      if (!user) { location.hash = '#auth'; return; }
      const purchased = store.orders.some(o => o.userId === user.id && o.items.some(it => it.productId === id));
      if (!purchased) { alert('Only customers who purchased this product can write a review.'); return; }
      const rating = Number(prompt('Rating 1-5', '5'));
      const comment = prompt('Comment', 'Loved it!') || '';
      if (!rating || rating < 1 || rating > 5) return;
      store.reviews.push({ id: store.seq.review++, productId: id, userId: user.id, rating, comment });
      save();
      renderProduct(id);
    });

    const list = document.getElementById('reviewsList');
    if (!productReviews.length) list.innerHTML = '<div class="muted">No reviews yet.</div>';
    else list.innerHTML = productReviews.map(r => {
      const u = store.users.find(u => u.id === r.userId);
      return `<div class="card"><div class="row" style="justify-content:space-between;">
        <strong>${u ? u.name : 'User'}</strong>
        <span class="rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
      </div><p>${r.comment}</p></div>`;
    }).join('');
  }

  function renderCart() {
    const items = store.cart.map(ci => ({ ...ci, product: store.products.find(p => p.id === ci.productId) })).filter(ci => ci.product);
    const total = items.reduce((s, i) => s + i.product.price * i.qty, 0);
    root.innerHTML = `
      <section class="stack">
        <h2>Shopping Cart</h2>
        <div class="panel">
          ${items.length === 0 ? '<div class="muted">Your cart is empty.</div>' : `
          <table>
            <thead><tr><th>Product</th><th>Qty</th><th>Price</th><th>Subtotal</th><th></th></tr></thead>
            <tbody>
            ${items.map(i => `
              <tr data-id="${i.productId}">
                <td>${i.product.title}</td>
                <td><input class="qty" type="number" min="1" value="${i.qty}" style="width:80px" /></td>
                <td>${formatPrice(i.product.price)}</td>
                <td>${formatPrice(i.product.price * i.qty)}</td>
                <td><button class="btn btn-danger remove">Remove</button></td>
              </tr>
            `).join('')}
            </tbody>
          </table>
          <div class="row" style="justify-content:space-between;align-items:center;margin-top:12px;">
            <a class="btn" href="#catalog">Continue shopping</a>
            <div class="row" style="gap:12px;align-items:center;">
              <strong>Total: ${formatPrice(total)}</strong>
              <a class="btn btn-primary" href="#checkout">Checkout</a>
            </div>
          </div>`}
        </div>
      </section>
    `;
    root.querySelectorAll('input.qty').forEach(inp => {
      inp.addEventListener('change', (e) => {
        const tr = e.target.closest('tr');
        const id = Number(tr.dataset.id);
        const qty = Math.max(1, Number(e.target.value) || 1);
        const item = store.cart.find(c => c.productId === id);
        if (item) item.qty = qty;
        save();
        renderCart();
        updateHeader();
      });
    });
    root.querySelectorAll('button.remove').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const tr = e.target.closest('tr');
        const id = Number(tr.dataset.id);
        store.cart = store.cart.filter(c => c.productId !== id);
        save();
        renderCart();
        updateHeader();
      });
    });
  }

  function renderCheckout() {
    if (store.cart.length === 0) { location.hash = '#cart'; return; }
    const user = getCurrentUser();
    const name = user ? user.name : '';
    root.innerHTML = `
      <section class="stack">
        <h2>Checkout</h2>
        <div class="split">
          <div class="panel">
            <h3>Shipping</h3>
            <div class="form">
              <div class="row">
                <div class="field" style="flex:1">
                  <label>Full name</label>
                  <input id="shipName" value="${name}" placeholder="Jane Doe" />
                </div>
                <div class="field" style="width:160px">
                  <label>Phone</label>
                  <input id="shipPhone" value="" placeholder="+1 555 000 000" />
                </div>
              </div>
              <div class="field"><label>Address</label><input id="shipAddr" placeholder="1 Main St" /></div>
              <div class="row">
                <div class="field" style="flex:1"><label>City</label><input id="shipCity" placeholder="City" /></div>
                <div class="field" style="width:120px"><label>ZIP</label><input id="shipZip" placeholder="00000" /></div>
              </div>
            </div>
          </div>
          <div class="panel">
            <h3>Payment</h3>
            <div class="form">
              <div class="field"><label>Card number</label><input id="cardNum" placeholder="4242 4242 4242 4242" /></div>
              <div class="row">
                <div class="field"><label>Exp</label><input id="cardExp" placeholder="12/29" /></div>
                <div class="field"><label>CVC</label><input id="cardCvc" placeholder="123" /></div>
              </div>
              <button id="placeOrder" class="btn btn-primary">Place Order</button>
            </div>
          </div>
        </div>
      </section>
    `;
    document.getElementById('placeOrder').addEventListener('click', () => {
      const orderId = store.seq.order++;
      const items = store.cart.map(ci => ({ productId: ci.productId, qty: ci.qty }));
      const total = items.reduce((s, i) => {
        const p = store.products.find(pp => pp.id === i.productId); return s + (p ? p.price * i.qty : 0);
      }, 0);
      const order = {
        id: orderId,
        number: `#${String(orderId).padStart(6, '0')}`,
        userId: store.session.userId,
        items,
        total,
        createdAt: new Date().toISOString(),
        status: 'Processing'
      };
      store.orders.unshift(order);
      store.cart = [];
      save();
      location.hash = '#confirm';
      updateHeader();
    });
  }

  function renderConfirmation() {
    const last = store.orders[0];
    root.innerHTML = `
      <section class="panel center" style="min-height:260px;">
        <div class="stack" style="place-items:center;">
          <h2>Thank you!</h2>
          <p class="muted">Your order ${last ? last.number : ''} has been placed.</p>
          <div class="row">
            <a class="btn" href="#orders">View Orders</a>
            <a class="btn btn-primary" href="#catalog">Continue Shopping</a>
          </div>
        </div>
      </section>
    `;
  }

  function renderOrders() {
    const user = getCurrentUser();
    const myOrders = user ? store.orders.filter(o => o.userId === user.id) : [];
    root.innerHTML = `
      <section class="stack">
        <h2>Orders</h2>
        <div class="panel">
          ${myOrders.length === 0 ? '<div class="muted">No orders yet.</div>' : `
          <table>
            <thead><tr><th>Order</th><th>Date</th><th>Status</th><th>Total</th></tr></thead>
            <tbody>
              ${myOrders.map(o => `<tr><td>${o.number}</td><td>${new Date(o.createdAt).toLocaleString()}</td><td>${o.status}</td><td>${formatPrice(o.total)}</td></tr>`).join('')}
            </tbody>
          </table>`}
        </div>
      </section>
    `;
  }

  function renderAuth() {
    root.innerHTML = `
      <section class="split">
        <div class="panel">
          <h3>Login</h3>
          <div class="form">
            <div class="field"><label>Email</label><input id="loginEmail" placeholder="alice@example.com" value="alice@example.com" /></div>
            <div class="field"><label>Password</label><input id="loginPass" type="password" placeholder="pass" value="pass" /></div>
            <button id="loginBtn" class="btn btn-primary">Login</button>
          </div>
        </div>
        <div class="panel">
          <h3>Register</h3>
          <div class="form">
            <div class="field"><label>Name</label><input id="regName" placeholder="Your name" /></div>
            <div class="field"><label>Email</label><input id="regEmail" placeholder="you@example.com" /></div>
            <div class="field"><label>Password</label><input id="regPass" type="password" placeholder="Choose a password" /></div>
            <button id="regBtn" class="btn">Create account</button>
          </div>
        </div>
      </section>
    `;
    document.getElementById('loginBtn').addEventListener('click', () => {
      const email = document.getElementById('loginEmail').value.trim();
      const pass = document.getElementById('loginPass').value;
      const u = store.users.find(u => u.email === email && u.password === pass);
      if (!u) { alert('Invalid credentials'); return; }
      store.session.userId = u.id; save();
      location.hash = '#profile';
      updateHeader();
    });
    document.getElementById('regBtn').addEventListener('click', () => {
      const name = document.getElementById('regName').value.trim();
      const email = document.getElementById('regEmail').value.trim();
      const pass = document.getElementById('regPass').value;
      if (!name || !email || !pass) { alert('Fill all fields'); return; }
      if (store.users.some(u => u.email === email)) { alert('Email already used'); return; }
      const id = store.seq.user++;
      store.users.push({ id, email, name, role: 'customer', password: pass });
      store.session.userId = id; save();
      location.hash = '#profile';
      updateHeader();
    });
  }

  function renderProfile() {
    const u = getCurrentUser();
    if (!u) { location.hash = '#auth'; return; }
    root.innerHTML = `
      <section class="stack">
        <div class="panel row" style="justify-content:space-between;align-items:center;">
          <div>
            <h3>Welcome, ${u.name}</h3>
            <div class="muted">${u.email} — Role: ${u.role}</div>
          </div>
          <div class="row">
            <a class="btn" href="#orders">Orders</a>
            <button id="logoutBtn" class="btn btn-ghost">Logout</button>
          </div>
        </div>
        <div class="panel">
          <h4>Addresses</h4>
          <div class="muted">(Demo) Add at checkout</div>
        </div>
      </section>
    `;
    document.getElementById('logoutBtn').addEventListener('click', () => {
      store.session.userId = null; save();
      location.hash = '#home';
      updateHeader();
    });
  }

  function renderSeller() {
    const u = getCurrentUser();
    if (!u) { location.hash = '#auth'; return; }
    if (u.role !== 'seller') {
      // Allow toggling role for demo purposes
      if (confirm('Switch your role to seller for demo?')) { u.role = 'seller'; save(); }
    }
    const myProducts = store.products.filter(p => (p.ownerId || 2) === u.id);
    root.innerHTML = `
      <section class="stack">
        <div class="panel">
          <h3>Seller Dashboard</h3>
          <div class="form">
            <div class="row">
              <div class="field" style="flex:1"><label>Title</label><input id="pTitle" /></div>
              <div class="field" style="width:160px"><label>Price</label><input id="pPrice" type="number" min="0" step="0.01" /></div>
              <div class="field" style="width:160px"><label>Stock</label><input id="pStock" type="number" min="0" step="1" value="10"/></div>
            </div>
            <div class="row">
              <div class="field" style="width:220px"><label>Category</label><input id="pCat" placeholder="Electronics"/></div>
              <div class="field" style="flex:1"><label>Image URL</label><input id="pImg" placeholder="https://..."/></div>
            </div>
            <div class="field"><label>Description</label><textarea id="pDesc" rows="3"></textarea></div>
            <button id="addProduct" class="btn btn-primary">Add Product</button>
          </div>
        </div>
        <div class="panel">
          <h4>Your Products</h4>
          <div class="grid" id="sellerGrid"></div>
        </div>
      </section>
    `;
    const grid = document.getElementById('sellerGrid');
    grid.innerHTML = myProducts.map(renderProductCard).join('');
    bindProductCardActions(grid);

    document.getElementById('addProduct').addEventListener('click', () => {
      const title = document.getElementById('pTitle').value.trim();
      const price = Number(document.getElementById('pPrice').value);
      const stock = Number(document.getElementById('pStock').value);
      const category = document.getElementById('pCat').value.trim() || 'Misc';
      const image = document.getElementById('pImg').value.trim() || 'https://images.unsplash.com/photo-1519337265831-281ec6cc8514?q=80&w=1200&auto=format&fit=crop';
      const description = document.getElementById('pDesc').value.trim();
      if (!title || !price || price < 0) { alert('Provide title and valid price'); return; }
      const id = store.seq.product++;
      store.products.unshift({ id, title, price, stock: Math.max(0, stock||0), category, image, description, rating: 4, ownerId: u.id });
      save();
      renderSeller();
    });
  }

  function addToCart(productId, qty) {
    const item = store.cart.find(c => c.productId === productId);
    if (item) item.qty += qty; else store.cart.push({ productId, qty });
    save();
  }

  // Search
  if (searchBtn) searchBtn.addEventListener('click', () => { location.hash = '#catalog'; });
  if (searchInput) searchInput.addEventListener('keydown', (e) => { if (e.key === 'Enter') location.hash = '#catalog'; });

  // Router
  window.addEventListener('hashchange', () => navigate(location.hash));
  navigate(location.hash || '#home');
})();


