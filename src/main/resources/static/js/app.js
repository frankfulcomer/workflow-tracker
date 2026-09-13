const itemsBody = document.getElementById('items-body');
const createForm = document.getElementById('create-form');
const createError = document.getElementById('create-error');
const searchBox = document.getElementById('search-box');
const statusFilter = document.getElementById('status-filter');
const ownerSelect = document.getElementById('owner');

const detailModal = document.getElementById('item-detail-modal');
const detailTitle = document.getElementById('detail-title');
const detailDescription = document.getElementById('detail-description');
const detailStatusBadge = document.getElementById('detail-status-badge');
const detailOwnerSelect = document.getElementById('detail-owner');
const detailCreated = document.getElementById('detail-created');
const detailUpdated = document.getElementById('detail-updated');
const detailHistoryBody = document.getElementById('detail-history-body');
const detailCloseBtn = document.getElementById('detail-close-btn');
const saveChangesBtn = document.getElementById('save-changes-btn');

const STATUSES = ['NEW', 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

let owners = [];
let latestRequestId = 0;

async function fetchOwners() {
    const res = await fetch('/api/owners');
    owners = await res.json();
    populateOwnerSelect(ownerSelect, null);
}

function populateOwnerSelect(select, selectedOwnerId) {
    select.innerHTML = '';
    const unassignedOpt = document.createElement('option');
    unassignedOpt.value = '';
    unassignedOpt.textContent = 'Unassigned';
    select.appendChild(unassignedOpt);

    for (const owner of owners) {
        const opt = document.createElement('option');
        opt.value = owner.id;
        opt.textContent = owner.name;
        if (selectedOwnerId != null && String(owner.id) === String(selectedOwnerId)) {
            opt.selected = true;
        }
        select.appendChild(opt);
    }
}

async function fetchItems() {
    const requestId = ++latestRequestId;
    const params = new URLSearchParams();
    if (statusFilter.value) params.set('status', statusFilter.value);
    if (searchBox.value.trim()) params.set('q', searchBox.value.trim());

    const res = await fetch(`/api/items?${params.toString()}`);
    const items = await res.json();

    // Discard this response if a newer fetchItems() call has since been made -
    // otherwise a slow, now-stale request (e.g. the unfiltered initial load)
    // can resolve after a subsequent filter/search and clobber its result.
    if (requestId !== latestRequestId) return;
    renderItems(items);
}

function renderItems(items) {
    itemsBody.innerHTML = '';
    for (const item of items) {
        const tr = document.createElement('tr');
        tr.setAttribute('data-testid', `item-row-${item.id}`);

        tr.innerHTML = `
            <td>${escapeHtml(item.title)}</td>
            <td>${escapeHtml(item.owner ? item.owner.name : '')}</td>
            <td><span class="badge ${item.status}" data-testid="item-status-${item.id}">${item.status.replace('_', ' ')}</span></td>
            <td>${formatDate(item.updatedDate)}</td>
            <td class="row-actions"></td>
        `;

        const actionsCell = tr.querySelector('.row-actions');

        const select = document.createElement('select');
        select.setAttribute('data-testid', `status-select-${item.id}`);
        for (const s of STATUSES) {
            const opt = document.createElement('option');
            opt.value = s;
            opt.textContent = s.replace('_', ' ');
            if (s === item.status) opt.selected = true;
            select.appendChild(opt);
        }
        select.addEventListener('change', () => updateStatus(item.id, select.value));
        actionsCell.appendChild(select);

        const viewBtn = document.createElement('button');
        viewBtn.type = 'button';
        viewBtn.textContent = 'View';
        viewBtn.className = 'secondary';
        viewBtn.setAttribute('data-testid', `view-btn-${item.id}`);
        viewBtn.addEventListener('click', () => openDetail(item.id));
        actionsCell.appendChild(viewBtn);

        const delBtn = document.createElement('button');
        delBtn.textContent = 'Delete';
        delBtn.className = 'secondary';
        delBtn.setAttribute('data-testid', `delete-btn-${item.id}`);
        delBtn.addEventListener('click', () => deleteItem(item.id));
        actionsCell.appendChild(delBtn);

        itemsBody.appendChild(tr);
    }
}

async function updateStatus(id, status) {
    const res = await fetch(`/api/items/${id}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
    });
    if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        alert(body.error || 'Could not update status');
    }
    fetchItems();
}

async function deleteItem(id) {
    await fetch(`/api/items/${id}`, { method: 'DELETE' });
    fetchItems();
}

let currentDetailId = null;
let originalTitle = '';
let originalOwnerId = null;
let originalDescription = '';

function populateDetailModal(item) {
    currentDetailId = item.id;
    originalTitle = item.title;
    originalOwnerId = item.owner ? item.owner.id : null;
    originalDescription = (item.description || '').trim();

    detailTitle.value = item.title;
    detailDescription.value = item.description || '';
    detailStatusBadge.textContent = item.status.replace('_', ' ');
    detailStatusBadge.className = `badge ${item.status}`;
    detailCreated.textContent = formatDate(item.createdDate);
    detailUpdated.textContent = formatDate(item.updatedDate);

    populateOwnerSelect(detailOwnerSelect, originalOwnerId);

    detailHistoryBody.innerHTML = '';
    for (const entry of item.statusHistory) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${entry.previousStatus.replace('_', ' ')}</td>
            <td>${entry.newStatus.replace('_', ' ')}</td>
            <td>${formatDate(entry.changedAt)}</td>
        `;
        detailHistoryBody.appendChild(tr);
    }

    updateSaveButtonState();
}

async function openDetail(id) {
    const res = await fetch(`/api/items/${id}`);
    if (!res.ok) return;
    const item = await res.json();
    populateDetailModal(item);
    detailModal.classList.remove('hidden');
}

function currentOwnerSelectValue() {
    return detailOwnerSelect.value ? Number(detailOwnerSelect.value) : null;
}

function isDetailDirty() {
    return detailTitle.value.trim() !== originalTitle
        || currentOwnerSelectValue() !== originalOwnerId
        || detailDescription.value.trim() !== originalDescription;
}

function updateSaveButtonState() {
    saveChangesBtn.disabled = !isDetailDirty();
}

function closeDetail() {
    if (isDetailDirty()) {
        const discard = confirm('You have unsaved changes. Discard them?');
        if (!discard) return;
    }
    detailModal.classList.add('hidden');
    currentDetailId = null;
}

async function saveChanges() {
    if (currentDetailId == null) return;
    const res = await fetch(`/api/items/${currentDetailId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: detailTitle.value.trim(),
            ownerId: currentOwnerSelectValue(),
            description: detailDescription.value.trim()
        })
    });
    if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        alert(body.error || 'Could not save changes');
        return;
    }
    detailModal.classList.add('hidden');
    currentDetailId = null;
    fetchItems();
}

detailTitle.addEventListener('input', updateSaveButtonState);
detailOwnerSelect.addEventListener('change', updateSaveButtonState);
detailDescription.addEventListener('input', updateSaveButtonState);
detailCloseBtn.addEventListener('click', closeDetail);
saveChangesBtn.addEventListener('click', saveChanges);

createForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    createError.textContent = '';

    const payload = {
        title: document.getElementById('title').value.trim(),
        ownerId: ownerSelect.value ? Number(ownerSelect.value) : null,
        description: document.getElementById('description').value.trim()
    };

    const res = await fetch('/api/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        createForm.reset();
        fetchItems();
    } else {
        createError.textContent = 'Could not create item - check the title.';
    }
});

searchBox.addEventListener('input', debounce(fetchItems, 300));
statusFilter.addEventListener('change', fetchItems);

function debounce(fn, delay) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => fn(...args), delay);
    };
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function formatDate(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleString();
}

fetchOwners();
fetchItems();
