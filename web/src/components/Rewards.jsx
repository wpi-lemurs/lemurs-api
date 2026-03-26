import React, { useState } from 'react';
import { Button, Form, Table } from 'react-bootstrap';

const initialRewards = [
    { id: 0, name: '80% Pariticipation Week 1-2', points: 10.00 },
    { id: 1, name: '80% Pariticipation Week 1-3', points: 15.00 },
    { id: 2, name: '80% Total Participation', points: 25.00 },
];

const emptyReward = { name: '', points: '' };

const Rewards = () => {
  const [rewards, setRewards] = useState(initialRewards);
  const [editing, setEditing] = useState(emptyReward);

  const handleSave = (e) => {
    e.preventDefault();
    if (editing.id !== undefined) {
      // Update existing reward
      setRewards(rewards.map(r => r.id === editing.id ? { ...editing, points: Number(editing.points) } : r));
    } else {
      // Add new reward
      const newId = rewards.length > 0 ? Math.max(...rewards.map(r => r.id)) + 1 : 0;
      setRewards([...rewards, { ...editing, id: newId, points: Number(editing.points) }]);
    }
    setEditing(emptyReward);
  };

  const handleDelete = (id) => {
    setRewards(rewards.filter(r => r.id !== id));
    if (editing.id === id) {
        setEditing(emptyReward);
    }
  };

  const startEdit = (reward) => {
    setEditing(reward);
  };

  const resetForm = () => {
    setEditing(emptyReward);
  };

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h3 className="mb-0">Rewards</h3>
      </div>

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
          {rewards.map((r) => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.name}</td>
              <td>{r.points}</td>
              <td className="d-flex gap-2 justify-content-center">
                <Button size="sm" variant="outline-primary" onClick={() => startEdit(r)}>Edit</Button>
                <Button size="sm" variant="outline-danger" onClick={() => handleDelete(r.id)}>Delete</Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <div className="mt-4">
        <h5>{editing.id !== undefined ? 'Edit Reward' : 'Add Reward'}</h5>
        <Form onSubmit={handleSave}>
          <Form.Group className="mb-2">
            <Form.Label>Reward Name</Form.Label>
            <Form.Control
              type="text"
              placeholder="Enter reward name"
              value={editing.name}
              onChange={(e) => setEditing({ ...editing, name: e.target.value })}
              required
            />
          </Form.Group>
          <Form.Group className="mb-2">
            <Form.Label>Points</Form.Label>
            <Form.Control
              type="number"
              placeholder="Enter points"
              value={editing.points}
              onChange={(e) => setEditing({ ...editing, points: e.target.value })}
              required
            />
          </Form.Group>
          <div className="d-flex gap-2 mt-2">
            <Button type="submit" variant="primary">
              {editing.id !== undefined ? 'Save Changes' : 'Add Reward'}
            </Button>
            <Button variant="secondary" onClick={resetForm}>Reset</Button>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default Rewards;
