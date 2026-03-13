import React, { useEffect, useState } from 'react';
import { Button, Form, Table, Alert, Spinner } from 'react-bootstrap';
import { getRewards, saveReward, deleteReward } from '../services/adminService';

const emptyReward = { id: '', name: '', points: 0 };

const Rewards = () => {
  const [rewards, setRewards] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [editing, setEditing] = useState(emptyReward);

  const load = async () => {
    setLoading(true);
    setMessage('');
    try {
      const res = await getRewards();
      setRewards(res.data || []);
    } catch (e) {
      setMessage('Failed to load rewards.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    try {
      const res = await saveReward({ ...editing, points: Number(editing.points) || 0 });
      setRewards(res.data || []);
      setEditing(emptyReward);
    } catch (err) {
      setMessage('Failed to save reward.');
    } finally {
      setLoading(false);
    }
  };

  const onDelete = async (id) => {
    setLoading(true);
    setMessage('');
    try {
      const res = await deleteReward(id);
      setRewards(res.data || []);
      if (editing.id === id) setEditing(emptyReward);
    } catch (err) {
      setMessage('Failed to delete reward.');
    } finally {
      setLoading(false);
    }
  };

  const startEdit = (r) => setEditing(r);
  const resetForm = () => setEditing(emptyReward);

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h3 className="mb-0">Rewards</h3>
        <Button variant="outline-secondary" size="sm" onClick={load} disabled={loading}>
          Refresh
        </Button>
      </div>

      {message && <Alert variant="warning">{message}</Alert>}
      {loading && (
        <div className="mb-2"><Spinner animation="border" size="sm" /> Loading…</div>
      )}

      <Table striped bordered hover size="sm" className="mt-2">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th>Name</th>
            <th style={{ width: '120px' }}>Points</th>
            <th style={{ width: '120px' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rewards.length === 0 ? (
            <tr><td colSpan={4} className="text-center">No rewards</td></tr>
          ) : (
            rewards.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>{r.name}</td>
                <td>{r.points}</td>
                <td className="d-flex gap-2 justify-content-center">
                  <Button size="sm" variant="outline-primary" onClick={() => startEdit(r)}>Edit</Button>
                  <Button size="sm" variant="outline-danger" onClick={() => onDelete(r.id)}>Delete</Button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </Table>

      <div className="mt-4">
        <h5>{editing.id ? 'Edit Reward' : 'Add Reward'}</h5>
        <Form onSubmit={onSubmit}>
          <Form.Group className="mb-2">
            <Form.Label>Reward name</Form.Label>
            <Form.Control
              type="text"
              value={editing.name}
              onChange={(e) => setEditing({ ...editing, name: e.target.value })}
              required
            />
          </Form.Group>
          <Form.Group className="mb-2">
            <Form.Label>Points</Form.Label>
            <Form.Control
              type="number"
              value={editing.points}
              onChange={(e) => setEditing({ ...editing, points: e.target.value })}
              required
              min={0}
            />
          </Form.Group>
          <div className="d-flex gap-2 mt-2">
            <Button type="submit" variant="primary" disabled={loading}>
              {editing.id ? 'Save Changes' : 'Add Reward'}
            </Button>
            <Button variant="secondary" onClick={resetForm} disabled={loading}>Reset</Button>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default Rewards;
