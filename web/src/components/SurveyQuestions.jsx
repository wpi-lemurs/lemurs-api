import React, { useContext, useEffect, useMemo, useState } from 'react';
import { Button, Form, Table, Alert, Spinner } from 'react-bootstrap';
import {
  getDailyMorningSurvey,
  getDailyAfternoonSurvey,
  getWeeklySurvey,
  saveSurveyQuestion,
  deleteSurveyQuestion,
} from '../services/adminService';
import { TokenContext } from './token/TokenContext';

const emptyQuestion = { id: '', question: '', type: 'text' };

const SurveyQuestions = () => {
  const { token } = useContext(TokenContext);
  const [kind, setKind] = useState('daily-morning'); // daily-morning | daily-afternoon | weekly
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [editing, setEditing] = useState(emptyQuestion);

  const fetcher = useMemo(() => {
    switch (kind) {
        case 'daily-morning':
            return getDailyMorningSurvey;
        case 'daily-afternoon':
            return getDailyAfternoonSurvey;
        case 'weekly':
            return getWeeklySurvey;
        default:
            return getDailyMorningSurvey;
    }
  }, [kind]);

  const load = async () => {
    setLoading(true);
    setMessage('');
    try {
      const res = await fetcher(token);
      if (res.status === 201) {
        setQuestions([]);
        setMessage(res.message || 'Survey not currently available. Try again within survey available times.');
      } else {
        setQuestions(res.data || []);
      }
    } catch (e) {
      setMessage('Failed to load survey questions.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [kind, token]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    try {
      const res = await saveSurveyQuestion(kind, editing, token);
      setQuestions(res.data || []);
      setEditing(emptyQuestion);
    } catch (err) {
      setMessage('Failed to save question.');
    } finally {
      setLoading(false);
    }
  };

  const onDelete = async (id) => {
    setLoading(true);
    setMessage('');
    try {
      const res = await deleteSurveyQuestion(kind, id, token);
      setQuestions(res.data || []);
      if (editing.id === id) setEditing(emptyQuestion);
    } catch (err) {
      setMessage('Failed to delete question.');
    } finally {
      setLoading(false);
    }
  };

  const startEdit = (q) => setEditing(q);
  const resetForm = () => setEditing(emptyQuestion);

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h3 className="mb-0">Survey Questions</h3>
        <div className="d-flex gap-2">
          <Form.Select
            aria-label="Survey kind"
            value={kind}
            onChange={(e) => setKind(e.target.value)}
            style={{ width: '180px' }}
          >
            <option value="daily-morning">Daily Morning</option>
            <option value="daily-afternoon">Daily Afternoon</option>
            <option value="weekly">Weekly</option>
          </Form.Select>
          <Button variant="outline-secondary" size="sm" onClick={load} disabled={loading}>
            Refresh
          </Button>
        </div>
      </div>

      {message && <Alert variant="warning">{message}</Alert>}
      {loading && (
        <div className="mb-2"><Spinner animation="border" size="sm" /> Loading…</div>
      )}

      <Table striped bordered hover size="sm" className="mt-2">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th>Question</th>
            <th style={{ width: '140px' }}>Type</th>
            <th style={{ width: '120px' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {questions.length === 0 ? (
            <tr><td colSpan={4} className="text-center">No questions</td></tr>
          ) : (
            questions.map((q) => (
              <tr key={q.id}>
                <td>{q.id}</td>
                <td>{q.question}</td>
                <td>{q.type}</td>
                <td className="d-flex gap-2 justify-content-center">
                  <Button size="sm" variant="outline-primary" onClick={() => startEdit(q)}>Edit</Button>
                  <Button size="sm" variant="outline-danger" onClick={() => onDelete(q.id)}>Delete</Button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </Table>

      <div className="mt-4">
        <h5>{editing.id ? 'Edit Question' : 'Add Question'}</h5>
        <Form onSubmit={onSubmit}>
          <Form.Group className="mb-2">
            <Form.Label>Question text</Form.Label>
            <Form.Control
              type="text"
              value={editing.question}
              onChange={(e) => setEditing({ ...editing, question: e.target.value })}
              required
            />
          </Form.Group>
          <Form.Group className="mb-2">
            <Form.Label>Type</Form.Label>
            <Form.Select
              value={editing.type}
              onChange={(e) => setEditing({ ...editing, type: e.target.value })}
            >
              <option value="text">Text</option>
              <option value="number">Number</option>
              <option value="mcq">Multiple Choice</option>
            </Form.Select>
          </Form.Group>
          <div className="d-flex gap-2 mt-2">
            <Button type="submit" variant="primary" disabled={loading}>
              {editing.id ? 'Save Changes' : 'Add Question'}
            </Button>
            <Button variant="secondary" onClick={resetForm} disabled={loading}>Reset</Button>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default SurveyQuestions;
